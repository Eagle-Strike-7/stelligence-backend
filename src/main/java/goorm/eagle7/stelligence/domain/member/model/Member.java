package goorm.eagle7.stelligence.domain.member.model;

import static jakarta.persistence.GenerationType.*;

import java.util.HashSet;
import java.util.Set;

import goorm.eagle7.stelligence.common.entity.BaseTimeEntity;
import goorm.eagle7.stelligence.domain.badge.model.Badge;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

	@Id
	@Column(name = "member_id")
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	// 기본값
	@Enumerated(EnumType.STRING)
	private Role role; // default: USER
	private long contributes; // default: 0

	@Column(columnDefinition = "tinyint")
	private boolean active; // default: true, for soft delete

	// social login 시 받아오는 정보
	private String nickname;
	private String email;
	private String imageUrl;
	private String socialId; // unique지만, DB에서는 unique 제약 조건을 걸지 않음.
	@Enumerated(EnumType.STRING)
	private SocialType socialType;

	// refresh token은 회원 가입/로그인 후 update로 진행
	private String refreshToken;

	/**
	 * <h2>MemberBadge 1:M 연관 관계 설정 (M)</h2>
	 * <p>- 중복 방지 위해 SET 사용</p>
	 * <p>- @ElementCollection: 값 타입 컬렉션 사용으로 Member와 생명 주기가 같음.</p>
	 * <p>- @Collectiontable: badges는 Member의 FK를 관리하는 테이블 생성</p>
	 * <p>- @Enumerated: EnumType.STRING으로 관리.</p>
	 */
	@ElementCollection
	@CollectionTable(
		name = "member_badge",
		joinColumns = @JoinColumn(name = "member_id")
	)
	@Enumerated(EnumType.STRING)
	@Column(name = "badges", columnDefinition = "varchar(30)")
	private Set<Badge> badges = new HashSet<>();

	/**
	 * <h2>Member는 정적 팩토리 메서드로 생성하기</h2>
	 * <p>member 생성 시, role은 USER, contributes는 0, active = true로  설정.</p>
	 * <p>refreshToken은 회원 가입 후 update로 진행</p>
	 * @param nickname 닉네임
	 * @param email 이메일
	 * @param imageUrl 프로필 사진 url
	 * @param socialId 소셜 id
	 * @param socialType 소셜 타입
	 */
	public static Member of(
		String nickname, String email,
		String imageUrl, String socialId,
		SocialType socialType) {

		Member member = new Member();

		member.nickname = nickname;
		member.email = email;
		member.imageUrl = imageUrl;
		member.socialId = socialId;
		member.socialType = socialType;

		// 기본값 설정
		member.refreshToken = null;
		member.role = Role.USER;
		member.contributes = 0;
		member.active = true;

		return member;

	}

	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void incrementContributes() {
		this.contributes++;
	}

	public void updateNickname(String nickname) {
		this.nickname = nickname;
	}

	public void inactivate() {
		this.active = false;
	}

	public void addBadge(Badge newBadge) {
		this.badges.add(newBadge);
	}

	public void expireRefreshToken() {
		this.refreshToken = null;
	}

	/**
	 * <h2>Member 탈퇴</h2>
	 * <p>- Member 정보 초기화, socialType WITHDRAWN으로 변경.</p>
	 * <p>- role은 그대로 유지(권한 문제), soft delete는 이미 처리되었어야 해서 건들지 않음.</p>
	 * <p>- badge는 new HashSet으로 초기화</p>
	 * @param newNickname "탈퇴한 회원NeutronStar"+ memberId
	 */
	public void withdraw(String newNickname) {

		this.nickname = newNickname;
		this.email = null;
		this.imageUrl = null;
		this.socialId = null;
		this.socialType = SocialType.WHITDRAWN;
		this.refreshToken = null;
		this.contributes = 0;
		this.badges = new HashSet<>();

	}

}
