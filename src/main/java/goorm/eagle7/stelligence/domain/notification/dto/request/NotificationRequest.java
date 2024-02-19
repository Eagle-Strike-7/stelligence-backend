package goorm.eagle7.stelligence.domain.notification.dto.request;

import java.util.Set;

import goorm.eagle7.stelligence.domain.notification.NotificationSender;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 알림 요청 DTO
 * <p>사용자로부터 받는 DTO가 아닌 다른 모듈로부터 받는 DTO입니다.
 * 알림을 보내고자 하는 모듈은 메시지와 URI, target을 설정하여 {@link NotificationSender}에게 전달해야합니다.
 */
@EqualsAndHashCode
@ToString
@Getter
@AllArgsConstructor(staticName = "of")
public class NotificationRequest {
	private String message;
	private String uri;
	private Set<Long> targets;
}
