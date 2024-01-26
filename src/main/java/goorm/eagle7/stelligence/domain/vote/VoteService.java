package goorm.eagle7.stelligence.domain.vote;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.vote.dto.VoteRequest;
import goorm.eagle7.stelligence.domain.vote.model.Vote;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VoteService {

	private final MemberRepository memberRepository;
	private final VoteRepository voteRepository;
	private final ContributeRepository contributeRepository;

	/**
	 * 투표 생성
	 * @param voteRequest
	 * @param loginMemberId
	 */
	public void vote(VoteRequest voteRequest, Long loginMemberId) {
		Member member = memberRepository.findById(loginMemberId).orElseThrow(
			() -> new BaseException("존재하지 않는 회원의 요청입니다. 사용자 ID: " + loginMemberId));

		Contribute contribute = contributeRepository.findById(voteRequest.getContributeId()).orElseThrow(
			() -> new BaseException("존재하지 않는 Contribute의 요청입니다. Contribute ID: " + voteRequest.getContributeId()));

		Vote vote = Vote.createVote(member, contribute, voteRequest.getAgree());
		voteRepository.save(vote);
	}

	/**
	 * 투표 삭제
	 * @param voteId
	 * @param loginMemberId
	 */
	public void cancelVote(Long voteId, Long loginMemberId) {
		Vote vote = voteRepository.findById(voteId).orElseThrow(
			() -> new BaseException("존재하지 않는 Vote의 요청입니다. Vote ID: " + voteId));

		// 해당 Vote를 생성한 사용자만 삭제 가능
		if (!vote.getMember().getId().equals(loginMemberId)) {
			throw new BaseException("해당 Vote를 삭제할 권한이 없습니다.");
		}

		voteRepository.delete(vote);
	}
}
