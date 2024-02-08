package goorm.eagle7.stelligence.domain.vote.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VoteTest {

	@Test
	@DisplayName("찬성을 취소한다.")
	void agreeToCancel() {

		Vote vote = Vote.createVote(null, null, true);

		vote.updateAgree(true);

		assertThat(vote.getAgree()).isNull();
	}

	@Test
	@DisplayName("취소한 상태에서 찬성한다.")
	void cancelToAgree() {

		Vote vote = Vote.createVote(null, null, null);

		vote.updateAgree(true);

		assertThat(vote.getAgree()).isTrue();
	}
}