package goorm.eagle7.stelligence.domain.contribute.scheduler;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.repository.DebateRepository;
import goorm.eagle7.stelligence.domain.document.content.model.Document;

@ExtendWith(MockitoExtension.class)
class DebateHandlerTest {

	@Mock
	private ContributeRepository contributeRepository;

	@Mock
	private DebateRepository debateRepository;

	@InjectMocks
	private DebateHandler debateHandler;

	@Test
	@DisplayName("투표중인 수정요청을 토론으로 전환")
	void debateHandling() {
		//given
		Long contributeId = 1L;
		Document document = document(1L, null, "title", null);
		Contribute contribute = contribute(contributeId, null, "title", "description", ContributeStatus.VOTING,
			document);

		when(contributeRepository.findById(contributeId)).thenReturn(Optional.of(contribute));

		//when
		debateHandler.handle(contributeId);

		//then
		// 토론으로 전환된 수정요청은 상태가 토론중으로 변경되며, 토론은 debateRepository에 의해 저장된다.
		assertThat(contribute.getStatus()).isEqualTo(ContributeStatus.DEBATING);
		verify(debateRepository, times(1)).save(any(Debate.class));
	}

	@Test
	@DisplayName("투표중인 상태가 아닌 수정요청을 토론으로 전환")
	void convertNotVotingContributeToDebate() {
		// given
		Long debatingContributeId = 1L;
		Long mergedContributeId = 2L;
		Long rejectedContributeId = 3L;

		Document document = document(1L, null, "title", null);
		Contribute debatingContribute = contribute(debatingContributeId, null, "title", "description",
			ContributeStatus.DEBATING, document);
		Contribute mergedContribute = contribute(mergedContributeId, null, "title", "description",
			ContributeStatus.MERGED, document);
		Contribute rejectedContribute = contribute(rejectedContributeId, null, "title", "description",
			ContributeStatus.REJECTED, document);

		when(contributeRepository.findById(debatingContributeId)).thenReturn(Optional.of(debatingContribute));
		when(contributeRepository.findById(mergedContributeId)).thenReturn(Optional.of(mergedContribute));
		when(contributeRepository.findById(rejectedContributeId)).thenReturn(Optional.of(rejectedContribute));

		// when

		// then
		// 토론중인 or 병합된 or 기각된 수정요청은 토론으로 전환할 수 없다.
		assertThatThrownBy(() -> debateHandler.handle(debatingContributeId))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("투표 중인 수정요청만 토론으로 전환할 수 있습니다.");
		assertThatThrownBy(() -> debateHandler.handle(mergedContributeId))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("투표 중인 수정요청만 토론으로 전환할 수 있습니다.");
		assertThatThrownBy(() -> debateHandler.handle(rejectedContributeId))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("투표 중인 수정요청만 토론으로 전환할 수 있습니다.");

		// 예외가 발생했으니 토론은 저장되지 않는다.
		verify(debateRepository, never()).save(any(Debate.class));
	}
}