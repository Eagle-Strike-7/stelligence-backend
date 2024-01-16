package goorm.eagle7.stelligence.domain.member.model;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import goorm.eagle7.stelligence.common.entity.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Member extends BaseTimeEntity {

	@Id
	@Column(name = "member_id")
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	// 기본값
	@Enumerated(EnumType.STRING)
	private Role role; // default: USER
	private long contributes; // default: 0

	// social login 시 받아오는 정보
	private String name;
	private String nickname; // TODO unique
	private String email;
	private String imageUrl;
	private String socialId; // unique

	@Enumerated(EnumType.STRING)
	private SocialType socialType; // default: kakao

	private String refreshToken;

	// 1:M 연관 관계 설정

	/**
	 * MemberBadge (M)
	 * enum으로 정의된 Badge를 member가 가질 수 있음.
	 * 중복 방지 위해 SET 사용,
	 * TODO badges에 add하는 메서드 필요
	 *
	 */
	@ElementCollection
	@Enumerated(EnumType.STRING)
	@CollectionTable(
		name = "member_badge",
		joinColumns = @JoinColumn(name = "member_id"))
	private Set<Badge> badges = new HashSet<>();

	// /*
	//  * Bookmark (M)
	//  * mappedBy: member, Bookmark 엔티티가 Member의 FK를 관리.
	//  * orphanRemoval: true, Member의 list에서 Bookmark이 방출되면 해당 Bookmark는 블랙홀로.
	//  * cascade: REMOVE, Member가 삭제되면 관련 있는 Bookmark들이 블랙홀로.
	//  * fetch: LAZY, Member를 조회한 후, bookmark를 조회할 때 bookmark 가져오기.
	//  * TODO bookmark 삭제 시 member에서도 삭제되는 건 bookmark에서 담당.
	//  */
	// @OneToMany(mappedBy = "member", orphanRemoval = true, cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	// private List<Bookmark> bookmarks = new ArrayList<>();

	// member 생성 시, role은 기본적으로 user로, contributes는 0으로, SocialType은 KAKAO로 설정.
	/*
	 * Member는 정적 팩토리 메서드로 생성하기
	 * @param name
	 * @param nickname // TODO service unique 검증 필요
	 * @param email
	 * @param imageUrl
	 * @param refreshToken
	 * @param socialId
	 * default: role: USER, contributes: 0, socialType: KAKAO
	 * // TODO 추후 2개 이상 구현 시 DEFault 아니고 필수로.
	 */
	public static Member of(String name, String nickname, String email, String imageUrl, String socialId) {
		Member member = new Member();
		member.name = name;
		member.nickname = nickname;
		member.email = email;
		member.imageUrl = imageUrl;
		member.socialId = socialId;

		// 기본값 설정
		member.socialType = SocialType.KAKAO;
		member.refreshToken = "";
		member.role = Role.USER;
		member.contributes = 0;
		return member;
	}

	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
