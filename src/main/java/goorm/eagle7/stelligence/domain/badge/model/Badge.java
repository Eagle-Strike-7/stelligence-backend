package goorm.eagle7.stelligence.domain.badge.model;

import static goorm.eagle7.stelligence.domain.badge.model.BadgeCategory.*;

import java.util.Arrays;

import lombok.Getter;

@Getter
public enum Badge {

	// enum type, value 필드, 생성자 순서가 일치해야 함.

	/** 글 작성 Badge 목록 **/
	ASTRONAUT("우주 여행자", WRITING, "첫 글 작성 완료", "astronaut.png"),
	MOON("달 탐사 완료", WRITING, "글 5개 작성 완료", "moon.png"),
	MARS("화성 탐사 완료", WRITING, "글 10개 작성 완료", "mars.png"),
	URANUS("천왕성 탐사 완료", WRITING, "글 20개 작성 완료", "uranus.png"),

	/** 수정 요청 Badge 목록 **/
	MERCURY("수성 탐사 완료", CONTRIBUTE_ALL, "수정 1번 요청 완료", "mercury.png"),
	VENUS("금성 탐사 완료", CONTRIBUTE_ALL, "수정 5번 요청 완료", "venus.png"),
	NEPTUNE("해왕성 탐사 완료", CONTRIBUTE_ALL, "수정 10번 요청 완료", "neptune.png"),
	SUN("태양 탐사 완료", CONTRIBUTE_ALL, "수정 30번 요청 완료", "sun.png"),
	GALAXY("태양계", CONTRIBUTE_ALL, "수정 50번 요청 완료", "galaxy.png"),

	/** 수정 요청 반영 Badge 목록 **/
	JUPITER("목성 탐사 완료", CONTRIBUTE_MERGED, "수정 요청 1개 반영 완료", "jupiter.png"),
	SATURN("토성 탐사 완료", CONTRIBUTE_MERGED, "수정 요청 5개 반영 완료", "saturn.png"),
	PLUTO("명왕성 탐사 완료", CONTRIBUTE_MERGED, "수정 요청 10개 반영 완료", "pluto.png"),
	ANDROMEDA("안드로메다 정복", CONTRIBUTE_MERGED, "수정 50번 반영 완료", "andromeda.png"),

	/** 수정 요청 반려 Badge 목록 **/
	BLACKHOLE("블랙홀", CONTRIBUTE_REJECTED, "수정 n번 반려 완료", "blackhole.png"),

	/** 회원 가입 Badge 목록 **/
	SPROUT("우주 새싹", MEMBER_JOIN, "회원 가입 완료", "sprout.png"),

	/** 신고 Badge 목록 **/
	GUARD("우주 방범대", REPORT, "신고 10회 완료", "guard.png");

	private final String title; // 홈페이지에서 사용자가 보는 배지 이름
	private final BadgeCategory eventCategory; // 배지 획득 조건
	private final String description; // 배지 설명
	private final String imgFilename; // 배지 이미지 URN
	private static final String BADGE_FOLDER_NAME = "/badges/";

	// 생성자의 파라미터 순서에 따라 상기 enum value 순서가 결정됨.
	Badge(String title, BadgeCategory eventCategory, String description, String imgFilename) {
		this.title = title;
		this.eventCategory = eventCategory;
		this.description = description;
		this.imgFilename = imgFilename;
	}

	public static Badge findByEventCategory(BadgeCategory badgeCategory) {
		return Arrays.stream(values())
			.filter(b -> b.getEventCategory() == badgeCategory)
			.findAny()
			.orElse(null);
	}

	public String getImgFilename() {
		return BADGE_FOLDER_NAME + imgFilename;
	}
}
