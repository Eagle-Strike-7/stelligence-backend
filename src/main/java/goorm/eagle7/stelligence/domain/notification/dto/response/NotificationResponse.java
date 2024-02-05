package goorm.eagle7.stelligence.domain.notification.dto.response;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 알림 조회 응답 DTO
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationResponse {

	private Long notificationId;
	private String content;
	private boolean isRead; // 읽음 여부
	private String uri; // 알림 클릭 시 이동할 URI
	private LocalDateTime createdAt;
}
