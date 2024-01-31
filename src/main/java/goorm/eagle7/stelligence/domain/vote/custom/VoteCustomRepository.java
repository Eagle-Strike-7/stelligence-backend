package goorm.eagle7.stelligence.domain.vote.custom;

import goorm.eagle7.stelligence.domain.vote.VoteSummary;

/**
 * Vote의 데이터를 저장하고 조회하는 Repository 인터페이스 입니다.
 *
 */
public interface VoteCustomRepository {

	/**
	 * 현 시각 Contribute에 대한 투표 결과를 반환합니다.
	 * @param contributeId Contribute의 ID
	 * @return 투표 결과 (총 투표 수, 찬성 투표 수)
	 */
	VoteSummary getVoteSummary(Long contributeId);
}
