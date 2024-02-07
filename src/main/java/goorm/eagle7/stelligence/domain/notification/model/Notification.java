package goorm.eagle7.stelligence.domain.notification.model;

import goorm.eagle7.stelligence.common.entity.BaseTimeEntity;
import goorm.eagle7.stelligence.domain.member.model.Member;
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

	private String message;

	private String uri;

	// 읽음 여부
	private boolean isRead;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	private Notification(String message, String uri, Member member) {
		this.message = message;
		this.uri = uri;
		isRead = false;
		this.member = member;
	}

	public static Notification createNotification(String content, String uri, Member member) {
		return new Notification(content, uri, member);
	}

	public void read() {
		this.isRead = true;
	}
}
