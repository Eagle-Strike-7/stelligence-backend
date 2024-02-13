package goorm.eagle7.stelligence.domain.vote;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.vote.dto.VoteRequest;
import goorm.eagle7.stelligence.domain.vote.dto.VoteSummaryResponse;
import goorm.eagle7.stelligence.domain.vote.model.Vote;
import goorm.eagle7.stelligence.domain.vote.model.VoteSummary;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VoteService {

	private final MemberRepository memberRepository;
	private final VoteRepository voteRepository;
	private final ContributeRepository contributeRepository;
	private final RedisTemplate<String, Object> redisTemplate;

	private final static int VOTE_EXPIRATION_MINUTES = 5;

	/**
	 * 투표 하기
	 * @param voteRequest
	 * @param loginMemberId
	 */
	@Transactional
	public VoteSummaryResponse vote(VoteRequest voteRequest, Long loginMemberId) {
		if (voteRequest.getAgree() == null) {
			throw new BaseException("투표 요청은 찬성(true), 반대(false) 중 하나여야 합니다.");
		}

		Member member = memberRepository.findById(loginMemberId).orElseThrow(
			() -> new BaseException("존재하지 않는 회원의 요청입니다. 사용자 ID: " + loginMemberId));

		Contribute contribute = contributeRepository.findById(voteRequest.getContributeId()).orElseThrow(
			() -> new BaseException("존재하지 않는 Contribute의 요청입니다. Contribute ID: " + voteRequest.getContributeId()));

		// Contribute의 상태가 VOTING인지 확인
		if (!contribute.isVoting()) {
			throw new BaseException("투표가 종료되었거나 진행 중이지 않습니다.");
		}

		// Redis 키 생성
		String key = "vote:" + contribute.getId();
		HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();

		Optional<Vote> existingVote = voteRepository.findByMemberAndContribute(member, contribute);
		Boolean updatedVoteStatus; //투표 후 변경된 투표 상태

		if (existingVote.isPresent()) { //이미 투표한 경우 요청에 따라 변경
			Vote vote = existingVote.get();
			Boolean previousVoteStatus = vote.getAgree(); //이전 투표 상태
			vote.updateAgree(voteRequest.getAgree());
			updatedVoteStatus = vote.getAgree(); //변경된 투표 상태

			//Redis 업데이트 로직
			updateVoteCountInRedis(hashOps, key, previousVoteStatus, updatedVoteStatus);
		} else { //처음 투표하는 경우 새로 생성
			Vote vote = Vote.createVote(member, contribute, voteRequest.getAgree());
			voteRepository.save(vote);
			updatedVoteStatus = vote.getAgree();

			//Redis 업데이트 로직
			updateVoteCountInRedis(hashOps, key, null, updatedVoteStatus);
		}

		redisTemplate.expire(key, Duration.ofMinutes(VOTE_EXPIRATION_MINUTES));

		// Redis에서 값을 조회하고, 값이 없으면 "0"으로 처리
		String agreeCountStr = hashOps.get(key, "agree");
		String disagreeCountStr = hashOps.get(key, "disagree");

		// 문자열을 int로 변환, 값이 없는 경우 0으로 초기화
		int agreeCount = agreeCountStr != null ? Integer.parseInt(agreeCountStr) : 0;
		int disagreeCount = disagreeCountStr != null ? Integer.parseInt(disagreeCountStr) : 0;

		return VoteSummaryResponse.of(
			agreeCount,
			disagreeCount,
			updatedVoteStatus
		);
	}

	private void updateVoteCountInRedis(HashOperations<String, String, String> hashOps, String key,
		Boolean previousVote, Boolean updatedVote) {

		if (previousVote != null) { //이전에 투표가 되어있는 경우(agree, disagree 중 하나로 저장되어 있음)
			if (updatedVote == null) {
				// 이전에 투표했던 것을 취소하는 경우
				hashOps.increment(key, previousVote ? "agree" : "disagree", -1);
			} else if (!previousVote.equals(updatedVote)) {
				// 이전 투표와 다른 선택을 한 경우
				hashOps.increment(key, previousVote ? "agree" : "disagree", -1); // 이전 선택 취소
				hashOps.increment(key, updatedVote ? "agree" : "disagree", 1); // 새로운 선택 반영
			}
		} else if (updatedVote != null) {
			// null에서 찬성 또는 반대로 변경하는 경우
			hashOps.increment(key, updatedVote ? "agree" : "disagree", 1);
		}

	}

	/**
	 * 투표 현황 조회
	 * @param contributeId
	 * @return 투표 현황 (찬성 개수, 반대 개수)
	 */
	public VoteSummaryResponse getVoteSummary(Long contributeId, Long loginMemberId) {

		Contribute contribute = contributeRepository.findById(contributeId).orElseThrow(
			() -> new BaseException("존재하지 않는 Contribute의 요청입니다. Contribute ID: " + contributeId));

		String key = "vote:" + contribute.getId();
		HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();

		Boolean keyExists = redisTemplate.hasKey(key);
		int agreeCount;
		int disagreeCount;

		if (!keyExists) {
			// Redis에 정보가 없는 경우, 데이터베이스에서 정보 조회 및 Redis에 저장
			VoteSummary voteSummary = voteRepository.getVoteSummary(contributeId);
			agreeCount = voteSummary.getAgreeCount();
			disagreeCount = voteSummary.getDisagreeCount();

			hashOps.put(key, "agree", String.valueOf(agreeCount));
			hashOps.put(key, "disagree", String.valueOf(disagreeCount));

			// 키에 대한 만료 시간 설정
			redisTemplate.expire(key, Duration.ofMinutes(VOTE_EXPIRATION_MINUTES));
		} else {
			// Redis에서 값을 조회하고, 값이 없으면 "0"으로 처리
			String agreeCountStr = hashOps.get(key, "agree");
			String disagreeCountStr = hashOps.get(key, "disagree");

			// 문자열을 int로 변환, 값이 없는 경우 0으로 초기화
			agreeCount = agreeCountStr != null ? Integer.parseInt(agreeCountStr) : 0;
			disagreeCount = disagreeCountStr != null ? Integer.parseInt(disagreeCountStr) : 0;
		}

		Boolean userVoteStatus = getUserVoteStatus(loginMemberId, contribute);

		return VoteSummaryResponse.of(
			agreeCount,
			disagreeCount,
			userVoteStatus
		);
	}

	private Boolean getUserVoteStatus(Long loginMemberId, Contribute contribute) {
		Boolean userVoteStatus = null; //사용자의 투표 상태(기본값: null)
		if (loginMemberId != null) { //로그인한 경우
			Member member = memberRepository.findById(loginMemberId).orElseThrow(
				() -> new BaseException("존재하지 않는 회원의 요청입니다. 사용자 ID: " + loginMemberId));

			Optional<Vote> vote = voteRepository.findByMemberAndContribute(member, contribute);

			if (vote.isPresent()) {
				userVoteStatus = vote.get().getAgree();
			}
		}
		return userVoteStatus;
	}
}
