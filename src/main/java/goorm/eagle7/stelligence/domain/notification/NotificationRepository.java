package goorm.eagle7.stelligence.domain.notification;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import goorm.eagle7.stelligence.domain.notification.custom.CustomNotificationRepository;
import goorm.eagle7.stelligence.domain.notification.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long>, CustomNotificationRepository {

	/**
	 * 특정 회원의 알림 목록 조회
	 * @param memberId 회원 ID
	 * @return 알림 목록
	 */
	@Query("select n from Notification n"
		+ " join fetch n.content"
		+ " where n.memberId = :memberId"
		+ " order by n.createdAt desc")
	List<Notification> findByMemberIdOrderByCreatedAtDesc(Long memberId);

	/**
	 * 특정 회원의 모든 알림을 읽음 처리
	 * @param memberId 회원 ID
	 */
	@Modifying
	@Query("update Notification n set n.isRead = true where n.memberId = :memberId")
	void readAllNotificationsByMemberId(Long memberId);

	/**
	 * 특정 회원의 알림 목록 삭제
	 * @param memberId 회원 ID
	 */
	void deleteAllByMemberId(Long memberId);
}
