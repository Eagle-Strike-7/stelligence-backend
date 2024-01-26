package goorm.eagle7.stelligence.domain.contribute.scheduler;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;

class RejectHandlerTest {

	@Test
	void handleTest() {
		//given
		RejectHandler rejectHandler = new RejectHandler();
		Contribute contribute = contribute(1L, null, ContributeStatus.VOTING, null);

		//when
		rejectHandler.handle(contribute);

		//then
		assertThat(contribute.getStatus()).isEqualTo(ContributeStatus.REJECTED);
	}
}