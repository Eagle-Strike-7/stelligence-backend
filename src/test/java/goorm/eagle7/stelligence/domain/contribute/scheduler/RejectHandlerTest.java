package goorm.eagle7.stelligence.domain.contribute.scheduler;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;

@ExtendWith(MockitoExtension.class)
class RejectHandlerTest {

	@Mock
	ContributeRepository contributeRepository;

	@InjectMocks
	RejectHandler rejectHandler;

	@Test
	void handleTest() {
		//given
		Contribute contribute = contribute(1L, null, ContributeStatus.VOTING, null);

		//when
		when(contributeRepository.findById(contribute.getId())).thenReturn(java.util.Optional.of(contribute));
		rejectHandler.handle(contribute.getId());

		//then
		assertThat(contribute.getStatus()).isEqualTo(ContributeStatus.REJECTED);
	}
}