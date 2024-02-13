package goorm.eagle7.stelligence.domain.notification.listener.debate;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import goorm.eagle7.stelligence.domain.debate.event.DebateEndEvent;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.repository.CommentRepository;
import goorm.eagle7.stelligence.domain.debate.repository.DebateRepository;
import goorm.eagle7.stelligence.domain.notification.NotificationRequest;
import goorm.eagle7.stelligence.domain.notification.NotificationSender;
import goorm.eagle7.stelligence.domain.notification.util.StringSlicer;
import lombok.RequiredArgsConstructor;

/**
 * 토론 종료 이벤트를 수신하여 토론 종료에 따른 알림을 전송하는 리스너 클래스입니다.
 *
 * <p>이벤트 수신 시, 토론 종료에 따른 알림을 전송합니다.</p>
 * <p>알림 대상은 수정요청 작성자와 댓글을 작성한 사용자입니다.</p>
 */
@Component
@RequiredArgsConstructor
public class DebateEndNotifier {

	private final DebateRepository debateRepository;
	private final CommentRepository commentRepository;
	private final NotificationSender notificationSender;

	private static final String DEBATE_END_MESSAGE = "토론 '%s'이 종료되었습니다. 내용을 확인해보세요.";
	private static final String DEBATE_URI = "/debateList/%d";

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@TransactionalEventListener(DebateEndEvent.class)
	public void notifyDebateEnd(DebateEndEvent event) {
		Debate debate = debateRepository.findByIdWithContributeWithoutAmendment(event.debateId())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 토론입니다."));

		// 대상을 찾습니다.
		Set<Long> targets = new HashSet<>();

		targets.add(debate.getContribute().getMember().getId());
		targets.addAll(commentRepository.findCommenterIdByDebateId(debate.getId()));

		// 알림 요청 객체를 생성합니다.
		NotificationRequest request = NotificationRequest.of(
			String.format(DEBATE_END_MESSAGE, StringSlicer.slice(debate.getContribute().getTitle())),
			String.format(DEBATE_URI, debate.getId()),
			targets
		);

		// 알림을 전송합니다.
		notificationSender.send(request);
	}
}
