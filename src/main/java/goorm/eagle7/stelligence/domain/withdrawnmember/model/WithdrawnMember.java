package goorm.eagle7.stelligence.domain.withdrawnmember.model;

import java.time.LocalDateTime;

import goorm.eagle7.stelligence.common.entity.BaseTimeEntity;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.member.model.Role;
import goorm.eagle7.stelligence.domain.member.model.SocialType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WithdrawnMember extends BaseTimeEntity {
	@Id
	@Column(name = "withdrawn_member_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 기본 회원 정보
	@Enumerated(EnumType.STRING)
	private Role role;
	private long contributes;
	private String name;
	private String nickname;
	private String email;
	private String imageUrl;
	private String socialId;
	@Enumerated(EnumType.STRING)
	private SocialType socialType;

	// 가입 날짜, 탈퇴한 날짜
	// BaseTimeEntity도 상속받지만, 명시적으로 확인 위한 컬럼 생성
	private LocalDateTime joinedAt;
	private LocalDateTime withdrawnAt;

	// member가 삭제될 일 X
	// WithdrawnMember가 삭제되면 연관 관계 끊어짐
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "origin_member_id")
	private Member member;

	/**
	 * <h2>WithdrawnMember 생성</h2>
	 * <p>badges, bookmarks를 제외한 Member의 정보를 복사하여 WithdrawnMember를 생성.</p>
	 * <p>Member의 clear 호출, null로 변경 및 WITHDRAWN으로 role 변경.</p>
	 * @param member 탈퇴할 Member
	 * @return WithdrawnMember
	 */
	public static WithdrawnMember of(Member member) {
		WithdrawnMember withdrawnMember = new WithdrawnMember();

		withdrawnMember.role = member.getRole();
		withdrawnMember.contributes = member.getContributes();
		withdrawnMember.name = member.getName();
		withdrawnMember.nickname = member.getNickname();
		withdrawnMember.email = member.getEmail();
		withdrawnMember.imageUrl = member.getImageUrl();
		withdrawnMember.socialId = member.getSocialId();
		withdrawnMember.socialType = member.getSocialType();
		withdrawnMember.joinedAt = member.getCreatedAt();
		withdrawnMember.withdrawnAt = LocalDateTime.now();
		withdrawnMember.member = member;

		member.withdraw();

		return withdrawnMember;
	}

}
