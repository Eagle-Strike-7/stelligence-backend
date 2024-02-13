package goorm.eagle7.stelligence.domain.notification;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

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

	/**
	 * 특정 회원의 모든 알림을 읽음 처리
	 * @param member 회원
	 */
	@Modifying
	@Query("update Notification n set n.isRead = true where n.member = :member")
	void readAllNotificationsByMember(Member member);

	/**
	 * 특정 회원의 알림 목록 삭제
	 * @param member 회원
	 */
	void deleteAllByMember(Member member);
}
