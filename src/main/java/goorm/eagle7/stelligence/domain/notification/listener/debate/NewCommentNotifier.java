package goorm.eagle7.stelligence.domain.notification.listener.debate;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import goorm.eagle7.stelligence.common.util.Site;
import goorm.eagle7.stelligence.domain.debate.event.NewCommentEvent;
import goorm.eagle7.stelligence.domain.debate.model.Comment;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.repository.CommentRepository;
import goorm.eagle7.stelligence.domain.notification.NotificationRequest;
import goorm.eagle7.stelligence.domain.notification.NotificationSender;
import goorm.eagle7.stelligence.domain.notification.util.StringSlicer;
import lombok.RequiredArgsConstructor;

/**
 * 새로운 댓글이 추가될 때 알림을 보내는 리스너
 *
 * <p>알림의 대상 : 토론에 댓글을 달았던 사용자, 수정 요청자
 */
@Component
@RequiredArgsConstructor
public class NewCommentNotifier {

	private final CommentRepository commentRepository;
	private final NotificationSender notificationSender;

	private static final String NEW_COMMENT_MESSAGE = "토론 '%s'에 댓글이 달렸습니다. '%s'";

	/**
	 * 새로운 댓글이 추가될 때 알림을 보낸다.
	 *
	 * <p>새로운 댓글이 달리는 이벤트는 굉장히 빈번하게 발생할 수 있다고 판단하여, 쿼리 수를 최소화 하기 위해
	 * COMMENT - DEBATE - CONTRIBUTE - MEMBER 를 fetch join하여 사용한다.
	 *
	 * <p> 이벤트 댓글 작성자는 알림을 받지 않아야 한다.
	 *
	 * @param event 새로운 댓글이 추가된 이벤트
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@TransactionalEventListener(NewCommentEvent.class)
	public void onNewComment(NewCommentEvent event) {

		Comment comment = commentRepository.findByIdWithDebateAndContributeAndContributor(event.commentId())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

		Debate debate = comment.getDebate();

		//알림을 받을 대상을 찾는다.
		Set<Long> targets = new HashSet<>();

		targets.add(debate.getContribute().getMember().getId());
		targets.addAll(commentRepository.findCommenterIdByDebateId(debate.getId()));
		targets.remove(comment.getCommenter().getId()); // 댓글 작성자는 알림을 받지 않는다.

		//알림 요청 객체를 생성한다.
		NotificationRequest request = NotificationRequest.of(
			String.format(
				NEW_COMMENT_MESSAGE,
				StringSlicer.slice(debate.getContribute().getTitle()),
				StringSlicer.slice(comment.getContent())
			),
			Site.debate(debate.getId()),
			targets
		);

		//알림을 보낸다.
		notificationSender.send(request);
	}
}
