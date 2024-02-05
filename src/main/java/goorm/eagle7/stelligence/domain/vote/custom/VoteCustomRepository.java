package goorm.eagle7.stelligence.domain.vote.custom;

import goorm.eagle7.stelligence.domain.vote.model.VoteSummary;

/**
 * Vote의 데이터를 저장하고 조회하는 Repository 인터페이스 입니다.
 *
 */
public interface VoteCustomRepository {

	/**
	 * Contribute에 대한 투표 현황을 반환합니다.
	 * @param contributeId
	 * @return 투표 현황 (찬성 개수, 반대 개수)
	 */
	VoteSummary getVoteSummary(Long contributeId);
}
