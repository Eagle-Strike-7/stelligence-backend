package goorm.eagle7.stelligence.domain.notification.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 알림 삭제 요청 DTO
 */
@Schema(description = "삭제할 알림의 ID 목록")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class NotificationDeleteRequest {
	@Schema(description = "삭제할 알림의 ID 목록", example = "[1, 2, 3]")
	private List<Long> notificationIds;
}
