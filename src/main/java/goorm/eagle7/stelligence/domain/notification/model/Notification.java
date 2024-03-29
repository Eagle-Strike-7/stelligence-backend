package goorm.eagle7.stelligence.domain.notification.model;

import goorm.eagle7.stelligence.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Notification extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "notification_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "notification_content_id")
	private NotificationContent content;

	// 읽음 여부
	@Column(columnDefinition = "tinyint")
	private boolean isRead;

	//알림 대상 사용자
	private Long memberId;

	public void read() {
		this.isRead = true;
	}
}
