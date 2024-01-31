package goorm.eagle7.stelligence.domain.notification.dto.request;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 알림 읽음 처리 요청 DTO
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class NotificationReadRequest {
	private List<Long> notificationIds;
}
