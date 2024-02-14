package goorm.eagle7.stelligence.domain.notification.listener.debate;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.debate.event.DebateEndEvent;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.model.DebateStatus;
import goorm.eagle7.stelligence.domain.debate.repository.CommentRepository;
import goorm.eagle7.stelligence.domain.debate.repository.DebateRepository;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.notification.NotificationRequest;
import goorm.eagle7.stelligence.domain.notification.NotificationSender;

@ExtendWith(MockitoExtension.class)
class DebateEndNotifierTest {

	@Mock
	DebateRepository debateRepository;

	@Mock
	CommentRepository commentRepository;

	@Mock
	NotificationSender notificationSender;

	@InjectMocks
	DebateEndNotifier debateEndNotifier;

	@Test
	@DisplayName("토론 종료 알림을 전송한다.")
	void notifyDebateEnd() {
		// given
		Member contributor = member(1L, "Pete");
		Document document = document(1L, member(2L, "contributor"), "title", 10L);
		Contribute contribute = contribute(1L, contributor, "contributeTitle", "desc", ContributeStatus.DEBATING,
			document);
		Debate debate = debate(1L, contribute, DebateStatus.CLOSED, null, 1);

		// when
		when(debateRepository.findByIdWithContributeWithoutAmendment(1L)).thenReturn(Optional.of(debate));
		when(commentRepository.findCommenterIdByDebateId(1L)).thenReturn(Set.of(3L, 4L, 5L));
		debateEndNotifier.notifyDebateEnd(new DebateEndEvent(1L));

		// then
		verify(notificationSender).send(
			NotificationRequest.of(
				"토론 'contributeTitle'이 종료되었습니다. 내용을 확인해보세요.",
				"/debateList/1",
				Set.of(1L, 3L, 4L, 5L)
			)
		);
	}
}