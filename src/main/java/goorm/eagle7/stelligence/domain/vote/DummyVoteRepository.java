package goorm.eagle7.stelligence.domain.vote;

import org.springframework.stereotype.Component;

/**
 * 빈을 찾지 못해 에러가 발생하는 것을 방지하기 위해 임시 빈을 등록합니다.
 */
@Component
public class DummyVoteRepository implements VoteCustomRepository {
	@Override
	public VoteSummary getVoteSummary(Long contributeId) {
		return new VoteSummary(100, 80);
	}
}
