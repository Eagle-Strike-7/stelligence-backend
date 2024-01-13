package goorm.eagle7.stelligence.domain.member.model;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import goorm.eagle7.stelligence.common.entity.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE) // Builder를 위해 추가 TODO Builder 안 하면 지우기
public class Member extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	// 기본값
	@Enumerated(EnumType.STRING)
	private Role role; // default: USER
	private long contributes; // default: 0 // TODO BIGINT인데, 그럼 Long? long?

	// TODO 각 unique 등 설정 확인하기
	// social login 시 받아오는 정보
	private String name; // TODO unique, nickname?
	private String email; // TODO unique
	private String imageUrl;
	private String socialId; // TODO unique
	private SocialType socialType; // default: kakao

	private String refreshToken;

	// 1:M 연관 관계 설정

	/**
	 * Badge (M)
	 * mappedBy: member, Badge 엔티티가 Member의 FK를 관리.
	 * orphanRemoval: true, Member의 list에서 Badge가 방출되면 해당 Badge는 블랙홀로.
	 * cascade: REMOVE, Member가 삭제되면 관련 있는 Badge들이 블랙홀로.
	 * fetch: LAZY, Member를 조회한 후, badge를 조회할 때 badge 가져오기. TODO 불러올 때는 List 전체를 불러오는 거 맞겠지?
	 * TODO badge 삭제 시 member에서도 삭제되는 건 badge에서 담당.
	 *
	 */
	@OneToMany(mappedBy = "member", orphanRemoval = true, cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private List<Badge> badges = new ArrayList<>();

	/*
	 * Bookmark (M)
	 * mappedBy: member, Bookmark 엔티티가 Member의 FK를 관리.
	 * orphanRemoval: true, Member의 list에서 Bookmark이 방출되면 해당 Bookmark는 블랙홀로.
	 * cascade: REMOVE, Member가 삭제되면 관련 있는 Bookmark들이 블랙홀로.
	 * fetch: LAZY, Member를 조회한 후, bookmark를 조회할 때 bookmark 가져오기. TODO 불러올 때는 List 전체를 불러오는 거 맞겠지?
	 * TODO bookmark 삭제 시 member에서도 삭제되는 건 bookmark에서 담당.
	 */
	@OneToMany(mappedBy = "member", orphanRemoval = true, cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private List<Bookmark> bookmarks = new ArrayList<>();

	// TODO 하기 코드 다 삭제하는 거 맞는지 확인하기
	// /*
	//  * Vote (M)
	//  * OnDelete: SET_NULL, Member가 삭제되면 Vote의 member를 null로 설정. TODO null인 경우 어떻게 되는지 담당자에게 확인하기
	//  * mappedBy: member, Vote 엔티티가 Member의 FK를 관리.
	//  * orphanRemoval: false, Member의 list에서 Vote가 방출될 일이 없고, 그렇다 한들 살아 있어야 함.
	//  * cascade: X, 대신 OnDelete로 설정.
	//  * fetch: LAZY, Member를 조회한 후, vote를 조회할 때 vote 가져오기. TODO 불러올 때는 List 전체를 불러오는 거 맞겠지?
	//  * TODO vote 삭제 시 member에서도 삭제되는 건 vote에서 담당.
	//  *
	//  */
	// @OnDelete(action = OnDeleteAction.SET_NULL)
	// @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
	// private List<Vote> votes = new ArrayList<>();
	//
	// /**
	//  * Commit (M)
	//  * mappedBy: member, Commit 엔티티가 Member의 FK를 관리.
	//  * orphanRemoval: true, Member의 list에서 Commit이 방출되면 해당 Commit은 블랙홀로.
	//  * cascade: REMOVE, Member가 삭제되면 관련 있는 Commit들이 블랙홀로. TODO Contributes(수정안)에 반영된 건 그대로 남아 있나?
	//  * fetch: LAZY, Member를 조회한 후, commit을 조회할 때 commit 가져오기. TODO 불러올 때는 List 전체를 불러오는 거 맞겠지?
	//  * TODO commit 삭제 시 member에서도 삭제되는 건 commit에서 담당.
	//  */
	// @OneToMany(mappedBy = "member", orphanRemoval = true, cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	// private List<Commit> commits = new ArrayList<>();
	//
	// /**
	//  * Section (M)
	//  * OnDelete: SET_NULL, Member가 삭제되면 Section의 member를 null로 설정. TODO null인 경우 어떻게 되는지 담당자에게 확인하기
	//  * mappedBy: member, Section 엔티티가 Member의 FK를 관리.
	//  * orphanRemoval: false, Member의 list에서 Section이 방출될 수 없고, 그렇다 한들 살아 있어야 함.
	//  * cascade: X, Member가 삭제되면 관련 있는 Section의 Member는 null로.
	//  * fetch: LAZY, Member를 조회한 후, section을 조회할 때 section 가져오기. TODO 불러올 때는 List 전체를 불러오는 거 맞겠지?
	//  * TODO section 삭제 시 member에서도 삭제되는 게 맞나? 이건 어떻게 처리할지 확인하기. 삭제된 section을 아예 delete하면 member에서도 삭제, 삭제된 것도 db에 남아 있다면, member에서 삭제되지는 않고, 목록에서는 보여줄 수 있어야 할까?
	//  *
	//  */
	// @OnDelete(action = OnDeleteAction.SET_NULL)
	// @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
	// private List<Section> sections = new ArrayList<>();
	//
	//
	//
	// /*
	//  * Report (M)
	//  * OnDelete: SET_NULL, Member가 삭제되면 Report의 member를 null로 설정. TODO null인 경우 어떻게 되는지 담당자에게 확인하기
	//  * mappedBy: member, Report 엔티티가 Member의 FK를 관리.
	//  * orphanRemoval: false, Member의 list에서 Report이 방출돼도 report는 살아 있음.
	//  * cascade: X, Member가 삭제되면 관련 있는 Report에서 Member를 null로.
	//  * fetch: LAZY, Member를 조회한 후, report를 조회할 때 report 가져오기. TODO 불러올 때는 List 전체를 불러오는 거 맞겠지?
	//  * TODO report 삭제 시 member에서도 삭제되는 건 report에서 담당.
	//  */
	// @OnDelete(action = OnDeleteAction.SET_NULL)
	// @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
	// private List<Report> reports = new ArrayList<>();

	// @Builder(builderMethodName = "innerBuilder") TODO Builder X?
	// member 생성 시, role은 기본적으로 user로, contributes는 0으로, SocialType은 KAKAO로 설정.
	/*
	 * Member는 생성자로 생성하기
	 * @param name
	 * @param email
	 * @param imageUrl
	 * @param refreshToken
	 * @param socialId
	 * default: role: USER, contributes: 0, socialType: KAKAO
	 */
	// TODO Dto에 의존하지 않기 위해 파라미터 전부 받는 것 ㄱㅊ?
	public Member(String name, String email, String imageUrl, String refreshToken,
		String socialId) {
		this.name = name;
		this.email = email;
		this.imageUrl = imageUrl;
		this.refreshToken = refreshToken;
		this.socialId = socialId;

		// 기본값 설정
		this.role = Role.USER;
		this.socialType = SocialType.KAKAO;
		this.contributes = 0;
	}

	// public static Member.MemberBuilder builder(String name, String email, String imageUrl, String refreshToken,
	// 	String socialId) {
	// 	return innerBuilder()
	// 		.name(name)
	// 		.email(email)
	// 		.imageUrl(imageUrl)
	// 		.refreshToken(refreshToken)
	// 		.socialId(socialId);
	// }
	//
	// public static Member.MemberBuilder innerBuilder() {
	// 	return new MemberBuilder();
	// }

}
