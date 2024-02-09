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

	@Test
	@DisplayName("반대한 상태에서 찬성한다.")
	void disagreeToAgree() {

		Vote vote = Vote.createVote(null, null, false);

		vote.updateAgree(true);

		assertThat(vote.getAgree()).isTrue();
	}

	@Test
	@DisplayName("반대를 취소한다.")
	void disagreeToCancel() {

		Vote vote = Vote.createVote(null, null, false);

		vote.updateAgree(false);

		assertThat(vote.getAgree()).isNull();
	}

	@Test
	@DisplayName("취소한 상태에서 반대한다.")
	void cancelToDisagree() {

		Vote vote = Vote.createVote(null, null, null);

		vote.updateAgree(false);

		assertThat(vote.getAgree()).isFalse();
	}

	@Test
	@DisplayName("찬성한 상태에서 반대한다.")
	void agreeToDisagree() {

		Vote vote = Vote.createVote(null, null, true);

		vote.updateAgree(false);

		assertThat(vote.getAgree()).isFalse();
	}

	@Test
	@DisplayName("실패 - null로 updateAgree 호출")
	void failUpdateAgree() {

		Vote vote = Vote.createVote(null, null, null);

		assertThatThrownBy(() -> vote.updateAgree(null))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("실패 - null로 updateAgree 호출")
	void failUpdateAgree2() {

		Vote vote = Vote.createVote(null, null, true);

		assertThatThrownBy(() -> vote.updateAgree(null))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("실패 - null로 updateAgree 호출")
	void failUpdateAgree3() {

		Vote vote = Vote.createVote(null, null, false);

		assertThatThrownBy(() -> vote.updateAgree(null))
			.isInstanceOf(IllegalArgumentException.class);
	}

}