package goorm.eagle7.stelligence.domain.notification.listener;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.domain.contribute.event.ContributeDebatedEvent;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.model.DebateStatus;
import goorm.eagle7.stelligence.domain.debate.repository.DebateRepository;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.notification.NotificationRequest;
import goorm.eagle7.stelligence.domain.notification.NotificationSender;
import goorm.eagle7.stelligence.domain.notification.listener.contribute.ContributeDebatedNotifier;
import goorm.eagle7.stelligence.domain.vote.VoteRepository;

@ExtendWith(MockitoExtension.class)
class ContributeDebatedNotifierTest {

	@Mock
	DebateRepository debateRepository;

	@Mock
	NotificationSender notificationSender;

	@Mock
	VoteRepository voteRepository;

	@InjectMocks
	ContributeDebatedNotifier contributeDebatedNotifier;

	@Test
	void onContributeDebated() {
		//given
		Member member = member(5L, "nickname");
		Document document = document(1L, member, "documentContent", 1L);
		Contribute contribute = contribute(1L, member, "contributeTitle", "contributeDescription",
			ContributeStatus.DEBATING, document);
		Debate debate = debate(1L, contribute, DebateStatus.OPEN, null, 1);
		ContributeDebatedEvent event = new ContributeDebatedEvent(1L);

		//when

		when(debateRepository.findByIdWithContributeWithoutAmendment(1L)).thenReturn(Optional.of(debate));
		when(voteRepository.findVoters(1L)).thenReturn(Set.of(1L, 2L, 3L));
		contributeDebatedNotifier.onContributeDebated(event);

		//then
		verify(notificationSender).send(
			NotificationRequest.of(
				"수정요청 'contributeTitle'와 관련된 토론이 시작되었습니다! 토론을 확인해보세요.",
				"/debate-list/1",
				Set.of(1L, 2L, 3L, 5L)
			)
		);
	}
}