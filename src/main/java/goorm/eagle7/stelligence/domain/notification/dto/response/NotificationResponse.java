package goorm.eagle7.stelligence.domain.notification.dto.response;

import java.time.LocalDateTime;

import goorm.eagle7.stelligence.domain.notification.model.Notification;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 알림 조회 응답 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationResponse {

	private Long notificationId;
	private String message;
	private boolean isRead; // 읽음 여부
	private String uri; // 알림 클릭 시 이동할 URI
	private LocalDateTime createdAt;

	public static NotificationResponse of(Notification notification) {
		NotificationResponse resp = new NotificationResponse();
		resp.notificationId = notification.getId();
		resp.message = notification.getContent().getMessage();
		resp.isRead = notification.isRead();
		resp.uri = notification.getContent().getUri();
		resp.createdAt = notification.getCreatedAt();
		return resp;
	}
}
