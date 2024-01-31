package goorm.eagle7.stelligence.domain.notification;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.common.auth.memberinfo.Auth;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
import goorm.eagle7.stelligence.domain.notification.dto.request.NotificationDeleteRequest;
import goorm.eagle7.stelligence.domain.notification.dto.request.NotificationReadRequest;
import goorm.eagle7.stelligence.domain.notification.dto.response.NotificationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Notification 관련 API를 제공하는 컨트롤러 클래스입니다.
 */
@Tag(name = "Notification API", description = "알림 관련 API를 제공합니다.")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

	/**'
	 * 사용자의 알림 목록을 조회합니다.
	 * 별도의 페이지네이션 없이 제공합니다.
	 * @param memberInfo 로그인한 사용자의 정보
	 * @return 알림 목록
	 */
	@Operation(summary = "알림 조회", description = "로그인한 사용자의 알림을 조회합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "알림 조회 성공",
		useReturnTypeSchema = true
	)
	@GetMapping
	public ResponseTemplate<List<NotificationResponse>> getNotifications(
		@Auth MemberInfo memberInfo
	) {
		return ResponseTemplate.ok();
	}

	/**
	 * 사용자의 알림을 읽음 처리합니다.
	 * @param notificationReadRequest 읽음 처리할 알림 ID 목록
	 * @param memberInfo 로그인한 사용자의 정보
	 * @return 성공 여부
	 */
	@Operation(summary = "알림 읽음 처리", description = "사용자의 알림을 읽음 처리합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "알림 읽음 처리 성공",
		useReturnTypeSchema = true
	)
	@PatchMapping
	public ResponseTemplate<Void> readNotifications(
		@RequestBody NotificationReadRequest notificationReadRequest,
		@Auth MemberInfo memberInfo
	) {
		return ResponseTemplate.ok();
	}

	/**
	 * 사용자의 알림을 삭제합니다.
	 * @param notificationDeleteRequest 삭제할 알림 ID 목록
	 * @param memberInfo 로그인한 사용자의 정보
	 * @return 성공 여부
	 */
	@Operation(summary = "알림 삭제", description = "사용자의 알림을 삭제합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "알림 삭제 성공",
		useReturnTypeSchema = true
	)
	@DeleteMapping
	public ResponseTemplate<Void> deleteNotifications(
		@RequestBody NotificationDeleteRequest notificationDeleteRequest,
		@Auth MemberInfo memberInfo
	) {
		return ResponseTemplate.ok();
	}

}
