package goorm.eagle7.stelligence.domain.notification.listener.badge;

import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import goorm.eagle7.stelligence.common.util.Site;
import goorm.eagle7.stelligence.domain.badge.event.NewBadgeEvent;
import goorm.eagle7.stelligence.domain.badge.model.Badge;
import goorm.eagle7.stelligence.domain.notification.NotificationSender;
import goorm.eagle7.stelligence.domain.notification.dto.request.NotificationRequest;
import lombok.RequiredArgsConstructor;

/**
 * 새로운 배지를 획득한 사용자에게 알림을 전송하는 리스너 클래스입니다.
 */
@Component
@RequiredArgsConstructor
public class NewBadgeNotifier {

	private final NotificationSender notificationSender;

	private static final String NEW_BADGE_MESSAGE = "새로운 뱃지 '%s'를 획득하였습니다. 사유 : '%s'. 확인해보세요!!!";

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@TransactionalEventListener(NewBadgeEvent.class)
	public void onNewBadge(NewBadgeEvent event) {

		Badge badge = event.badge();

		// 알림 요청 객체를 생성합니다.
		NotificationRequest request = NotificationRequest.of(
			String.format(NEW_BADGE_MESSAGE, badge.getTitle(), badge.getDescription()),
			Site.mypage(),
			Set.of(event.memberId())
		);

		// 알림을 전송합니다.
		notificationSender.send(request);
	}
}
