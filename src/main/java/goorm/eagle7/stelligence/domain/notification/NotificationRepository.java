package goorm.eagle7.stelligence.domain.notification;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.notification.custom.CustomNotificationRepository;
import goorm.eagle7.stelligence.domain.notification.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long>, CustomNotificationRepository {

	/**
	 * 특정 회원의 알림 목록 조회
	 * @param member 회원
	 * @return 알림 목록
	 */
	List<Notification> findByMember(Member member);
}
