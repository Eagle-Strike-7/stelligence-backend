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
import goorm.eagle7.stelligence.domain.debate.event.NewCommentEvent;
import goorm.eagle7.stelligence.domain.debate.model.Comment;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.model.DebateStatus;
import goorm.eagle7.stelligence.domain.debate.repository.CommentRepository;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.notification.NotificationRequest;
import goorm.eagle7.stelligence.domain.notification.NotificationSender;

@ExtendWith(MockitoExtension.class)
class NewCommentNotifierTest {

	@Mock
	CommentRepository commentRepository;

	@Mock
	NotificationSender notificationSender;

	@InjectMocks
	NewCommentNotifier newCommentNotifier;

	@Test
	@DisplayName("수정요청의 작성자는 댓글 알림을 받아야하며, 댓글 작성자는 알림을 받지 않아야한다.")
	void notifierTest() {
		// given
		Member member = member(2L, "pete");
		Document document = document(1L, member, "documentTitle", 3L);
		Contribute contribute = contribute(1L, member, "contributeTitle", "contributeDescription",
			ContributeStatus.DEBATING, document); //contribute 작성자 : 2L
		Debate debate = debate(1L, contribute, DebateStatus.OPEN, null, 1);
		Comment comment = comment(1L, debate, member(1L, "hello"), "commentContent", 1); // comment 작성자 : 1L

		// when
		when(commentRepository.findCommenterIdByDebateId(1L))
			.thenReturn(Set.of(1L, 4L, 5L));
		when(commentRepository.findByIdWithDebateAndContributeAndContributor(1L))
			.thenReturn(Optional.of(comment));

		newCommentNotifier.onNewComment(new NewCommentEvent(1L));

		// then
		verify(notificationSender).send(
			NotificationRequest.of(
				"토론 'contributeTitle'에 댓글이 달렸습니다. 'commentContent'",
				"/debateList/1",
				Set.of(2L, 4L, 5L)
			)
		);
	}

}