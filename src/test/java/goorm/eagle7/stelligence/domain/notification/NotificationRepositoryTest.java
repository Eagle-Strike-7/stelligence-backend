package goorm.eagle7.stelligence.domain.notification;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.config.mockdata.WithMockData;
import goorm.eagle7.stelligence.domain.notification.model.Notification;

@SpringBootTest
@Transactional
@WithMockData
class NotificationRepositoryTest {

	@Autowired
	private NotificationRepository notificationRepository;

	@Test
	@DisplayName("여러 회원에게 알림을 한번에 추가한다.")
	void bulkInsert() {
		notificationRepository.insertNotifications("message", "uri", Set.of(1L, 2L, 4L));

		// then
		List<Notification> notifications = notificationRepository.findAll();
		assertThat(notifications)
			.hasSize(3)
			.extracting(Notification::getMemberId)
			.containsExactlyInAnyOrder(1L, 2L, 4L);
	}
}