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
import goorm.eagle7.stelligence.domain.document.content.model.Document;

@ExtendWith(MockitoExtension.class)
class RejectHandlerTest {

	@Mock
	ContributeRepository contributeRepository;

	@InjectMocks
	RejectHandler rejectHandler;

	@Test
	void handleTest() {
		//given
		Document document = document(1L, null, "title", null);
		Contribute contribute = contribute(1L, null, "title", "description", ContributeStatus.VOTING, document);

		//when
		when(contributeRepository.findById(contribute.getId())).thenReturn(java.util.Optional.of(contribute));
		rejectHandler.handle(contribute.getId());

		//then
		assertThat(contribute.getStatus()).isEqualTo(ContributeStatus.REJECTED);
	}
}