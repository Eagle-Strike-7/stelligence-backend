package goorm.eagle7.stelligence.domain.vote.custom;

import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.vote.VoteResultSummary;

/**
 * 빈을 찾지 못해 에러가 발생하는 것을 방지하기 위해 임시 빈을 등록합니다.
 */
@Component
public class DummyVoteRepository implements VoteCustomRepository {
	@Override
	public VoteResultSummary getVoteSummary(Long contributeId) {
		return new VoteResultSummary(100L, 100, 80);
	}
}
