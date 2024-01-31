package goorm.eagle7.stelligence.domain.vote;

import java.util.Optional;

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
	 * 투표 하기
	 * @param voteRequest
	 * @param loginMemberId
	 */
	public void vote(VoteRequest voteRequest, Long loginMemberId) {
		Member member = memberRepository.findById(loginMemberId).orElseThrow(
			() -> new BaseException("존재하지 않는 회원의 요청입니다. 사용자 ID: " + loginMemberId));

		Contribute contribute = contributeRepository.findById(voteRequest.getContributeId()).orElseThrow(
			() -> new BaseException("존재하지 않는 Contribute의 요청입니다. Contribute ID: " + voteRequest.getContributeId()));

		if (voteRequest.getAgree() == null) {
			throw new BaseException("투표 요청은 찬성(true), 반대(false) 중 하나여야 합니다.");
		}

		Optional<Vote> existingVote = voteRepository.findByMemberAndContribute(member, contribute);

		if (existingVote.isPresent()) { //이미 투표한 경우 요청에 따라 변경
			Vote vote = existingVote.get();
			vote.updateAgree(voteRequest.getAgree());
		} else { //처음 투표하는 경우 새로 생성
			Vote vote = Vote.createVote(member, contribute, voteRequest.getAgree());
			voteRepository.save(vote);
		}
	}
}
