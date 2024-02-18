package goorm.eagle7.stelligence.domain.vote;

import java.time.Duration;
import java.util.Map;
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
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class VoteService {

	private final MemberRepository memberRepository;
	private final VoteRepository voteRepository;
	private final ContributeRepository contributeRepository;
	private final RedisTemplate<String, Object> redisTemplate;

	private static final int VOTE_CACHE_EXPIRATION_MINUTES = 1;

	private static final String VOTE_KEY = "vote:";
	private static final String VOTE_STATUS_AGREE = "agree";
	private static final String VOTE_STATUS_DISAGREE = "disagree";

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

		Optional<Vote> existingVote = voteRepository.findByMemberAndContribute(member, contribute);
		Boolean updatedVoteStatus; //투표 후 변경된 투표 상태

		cacheVoteCount(contribute.getId()); //Redis에 투표 정보가 없는 경우, 데이터베이스에서 정보 조회 및 Redis에 저장

		//투표 상태 업데이트
		if (existingVote.isPresent()) { //이미 투표한 경우 요청에 따라 변경
			Vote vote = existingVote.get();
			Boolean previousVoteStatus = vote.getAgree(); //이전 투표 상태
			vote.updateAgree(voteRequest.getAgree());
			updatedVoteStatus = vote.getAgree(); //변경된 투표 상태

			//Redis 업데이트 로직
			updateVoteCountInRedis(contribute.getId(), previousVoteStatus, updatedVoteStatus);
		} else { //처음 투표하는 경우 새로 생성
			Vote vote = Vote.createVote(member, contribute, voteRequest.getAgree());
			voteRepository.save(vote);
			updatedVoteStatus = vote.getAgree();

			//Redis 업데이트 로직
			updateVoteCountInRedis(contribute.getId(), null, updatedVoteStatus);
		}

		int agreeCount = getVoteCountByVoteStatus(contribute.getId(), VOTE_STATUS_AGREE);
		int disagreeCount = getVoteCountByVoteStatus(contribute.getId(), VOTE_STATUS_DISAGREE);

		return VoteSummaryResponse.of(agreeCount, disagreeCount, updatedVoteStatus);
	}

	// Redis에서 투표 개수 조회
	private int getVoteCountByVoteStatus(Long contributeId, String voteStatus) {
		String key = VOTE_KEY + contributeId;
		HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();

		String agreeCountStr = hashOps.get(key, voteStatus);
		return agreeCountStr != null ? Integer.parseInt(agreeCountStr) : 0;
	}

	// Redis에 투표 정보가 없는 경우, 데이터베이스에서 정보 조회 및 Redis에 저장
	private void cacheVoteCount(Long contributeId) {
		String key = VOTE_KEY + contributeId;
		HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
		Boolean keyExists = redisTemplate.hasKey(key); //Redis에 키가 존재하는지 확인

		if (!keyExists) {
			VoteSummary voteSummary = voteRepository.getVoteSummary(contributeId);
			int agreeCount = voteSummary.getAgreeCount();
			int disagreeCount = voteSummary.getDisagreeCount();

			hashOps.put(key, VOTE_STATUS_AGREE, String.valueOf(agreeCount));
			hashOps.put(key, VOTE_STATUS_DISAGREE, String.valueOf(disagreeCount));
		}
	}

	// Redis에 투표 정보 업데이트
	private void updateVoteCountInRedis(Long contributeId, Boolean previousVote, Boolean updatedVote) {
		// Redis 키 생성
		String key = VOTE_KEY + contributeId;
		HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();

		if (previousVote != null) { //이전에 투표가 되어있는 경우(agree, disagree 중 하나로 저장되어 있음)
			if (updatedVote == null) {
				// 이전에 투표했던 것을 취소하는 경우
				hashOps.increment(key, previousVote ? VOTE_STATUS_AGREE : VOTE_STATUS_DISAGREE, -1);
			} else if (!previousVote.equals(updatedVote)) {
				// 이전 투표와 다른 선택을 한 경우
				hashOps.increment(key, previousVote ? VOTE_STATUS_AGREE : VOTE_STATUS_DISAGREE, -1); // 이전 선택 취소
				hashOps.increment(key, updatedVote ? VOTE_STATUS_AGREE : VOTE_STATUS_DISAGREE, 1); // 새로운 선택 반영
			}
		} else if (updatedVote != null) {
			// null에서 찬성 또는 반대로 변경하는 경우
			hashOps.increment(key, updatedVote ? VOTE_STATUS_AGREE : VOTE_STATUS_DISAGREE, 1);
		}

		redisTemplate.expire(key, Duration.ofMinutes(VOTE_CACHE_EXPIRATION_MINUTES));
	}

	/**
	 * 투표 현황 조회
	 * @param contributeId
	 * @return 투표 현황 (찬성 개수, 반대 개수)
	 */
	public VoteSummaryResponse getVoteSummary(Long contributeId, Long loginMemberId) {

		Contribute contribute = contributeRepository.findById(contributeId).orElseThrow(
			() -> new BaseException("존재하지 않는 Contribute의 요청입니다. Contribute ID: " + contributeId));

		// Redis에서 투표 현황 조회
		Map<String, Integer> voteCount = getVoteCountFromRedis(contributeId);

		// 사용자의 투표 상태 조회
		Boolean userVoteStatus = getUserVoteStatus(loginMemberId, contribute);

		return VoteSummaryResponse.of(
			voteCount.get(VOTE_STATUS_AGREE),
			voteCount.get(VOTE_STATUS_DISAGREE),
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

	private Map<String, Integer> getVoteCountFromRedis(Long contributeId) {
		String key = VOTE_KEY + contributeId;
		HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
		Boolean keyExists = redisTemplate.hasKey(key);

		if (!keyExists) {
			// Redis에 정보가 없는 경우, 데이터베이스에서 정보 조회 및 Redis에 저장
			VoteSummary voteSummary = voteRepository.getVoteSummary(contributeId);
			int agreeCount = voteSummary.getAgreeCount();
			int disagreeCount = voteSummary.getDisagreeCount();

			hashOps.put(key, VOTE_STATUS_AGREE, String.valueOf(agreeCount));
			hashOps.put(key, VOTE_STATUS_DISAGREE, String.valueOf(disagreeCount));

			// 키에 대한 만료 시간 설정
			redisTemplate.expire(key, Duration.ofMinutes(VOTE_CACHE_EXPIRATION_MINUTES));

			return Map.of(VOTE_STATUS_AGREE, agreeCount, VOTE_STATUS_DISAGREE, disagreeCount);
		} else {
			int agreeCount = getVoteCountByVoteStatus(contributeId, VOTE_STATUS_AGREE);
			int disagreeCount = getVoteCountByVoteStatus(contributeId, VOTE_STATUS_DISAGREE);

			return Map.of(VOTE_STATUS_AGREE, agreeCount, VOTE_STATUS_DISAGREE, disagreeCount);
		}
	}
}
