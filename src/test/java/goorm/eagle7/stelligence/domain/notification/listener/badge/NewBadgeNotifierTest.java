package goorm.eagle7.stelligence.domain.notification.listener.badge;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.domain.badge.event.NewBadgeEvent;
import goorm.eagle7.stelligence.domain.badge.model.Badge;
import goorm.eagle7.stelligence.domain.notification.NotificationRequest;
import goorm.eagle7.stelligence.domain.notification.NotificationSender;

@ExtendWith(MockitoExtension.class)
class NewBadgeNotifierTest {

	@Mock
	NotificationSender notificationSender;

	@InjectMocks
	NewBadgeNotifier newBadgeNotifier;

	@Test
	@DisplayName("새로운 뱃지 알림 전송")
	void onNewBadge() {

		// given
		NewBadgeEvent event = new NewBadgeEvent(1L, Badge.GALAXY);

		// when
		newBadgeNotifier.onNewBadge(event);

		// then
		Mockito.verify(notificationSender).send(
			NotificationRequest.of(
				"새로운 뱃지 '태양계'를 획득하였습니다. 사유 : '수정 50번 요청 완료'. 확인해보세요!!!",
				"/mypage",
				Set.of(1L)
			)
		);
	}
}