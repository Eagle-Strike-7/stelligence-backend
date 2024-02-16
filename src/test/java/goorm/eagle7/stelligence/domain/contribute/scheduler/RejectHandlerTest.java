package goorm.eagle7.stelligence.domain.contribute.scheduler;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.event.ContributeRejectedEvent;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.document.content.model.Document;

@ExtendWith(MockitoExtension.class)
class RejectHandlerTest {

	@Mock
	ContributeRepository contributeRepository;

	@Mock
	ApplicationEventPublisher applicationEventPublisher;

	@InjectMocks
	RejectHandler rejectHandler;

	@Test
	void handleTest() {
		//given
		Document document = document(1L, null, "title", null);
		Contribute contribute = contribute(1L, null, "title", "description", ContributeStatus.VOTING, document);

		when(contributeRepository.findById(contribute.getId())).thenReturn(Optional.of(contribute));

		// when
		rejectHandler.handle(contribute.getId());

		//then
		assertThat(contribute.getStatus()).isEqualTo(ContributeStatus.REJECTED);
		verify(applicationEventPublisher).publishEvent(new ContributeRejectedEvent(contribute.getId()));
	}
}