package goorm.eagle7.stelligence.domain.notification;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.notification.dto.response.NotificationResponse;
import goorm.eagle7.stelligence.domain.notification.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final MemberRepository memberRepository;

	/**
	 * 특정 회원의 알림 목록 조회
	 * @param memberId 회원 ID
	 * @return 알림 목록
	 */
	@Transactional(readOnly = true)
	public List<NotificationResponse> getNotificationsByMemberId(Long memberId) {

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BaseException("존재하지 않는 회원입니다."));

		return notificationRepository.findByMember(member).stream()
			.map(NotificationResponse::of)
			.toList();
	}

	/**
	 * 알림을 읽음 처리합니다.
	 * @param notificationId 알림 ID
	 * @param memberId 로그인한 사용자의 ID
	 */
	public void readNotification(Long notificationId, Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BaseException("존재하지 않는 회원입니다."));

		Notification notification = notificationRepository.findById(notificationId)
			.orElseThrow(() -> new BaseException("존재하지 않는 알림입니다."));

		if (!notification.getMember().equals(member)) {
			throw new BaseException("권한이 없습니다.");
		}

		notification.read();
	}

	/**
	 * 사용자의 알림을 일괄적으로 읽음 처리합니다.
	 * @param memberId 로그인한 사용자의 ID
	 */
	public void readAllNotifications(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BaseException("존재하지 않는 회원입니다."));

		notificationRepository.readAllNotificationsByMember(member);
	}

	/**
	 * 사용자의 알림을 개별 삭제합니다.
	 * @param notificationId 알림 ID
	 * @param memberId 로그인한 사용자의 ID
	 */
	public void deleteNotification(Long notificationId, Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BaseException("존재하지 않는 회원입니다."));

		Notification notification = notificationRepository.findById(notificationId)
			.orElseThrow(() -> new BaseException("존재하지 않는 알림입니다."));

		if (!notification.getMember().equals(member)) {
			throw new BaseException("권한이 없습니다.");
		}

		notificationRepository.delete(notification);
	}

	/**
	 * 사용자의 알림을 일괄 삭제합니다.
	 * @param memberId 로그인한 사용자의 ID
	 */
	public void deleteAllNotifications(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BaseException("존재하지 않는 회원입니다."));

		notificationRepository.deleteAllByMember(member);
	}
}
