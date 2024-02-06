package goorm.eagle7.stelligence.domain.badges.model;

import lombok.Getter;

@Getter
public enum Badge {

	// TODO 중복 "글", "수정 요청" 한번에 관리
	// enum type, value 필드, 생성자 순서가 일치해야 함.
	
	ASTRONAUT("우주 여행자", "글", "첫 글 작성 완료", "/badges/astronaut.png"),

	MOON("달 탐사 완료", "글", "글 5개 작성 완료", "/badges/moon.png"),

	MERCURY("수성 탐사 완료", "수정 요청", "수정 1번 요청 완료", "/badges/mercury.png"),

	VENUS("금성 탐사 완료", "수정 요청", "수정 5번 요청 완료", "/badges/venus.png"),

	MARS("화성 탐사 완료", "글", "글 10개 작성 완료", "/badges/mars.png"),

	JUPITER("목성 탐사 완료", "수정 요청 1개 반영 완료", "/badges/jupiter.png", "수정 요청"),

	SATURN("토성 탐사 완료", "수정 요청", "수정 요청 5개 반영 완료", "/badges/saturn.png"),

	URANUS("천왕성 탐사 완료", "글", "글 20개 작성 완료", "/badges/uranus.png"),

	NEPTUNE("해왕성 탐사 완료", "수정 요청", "수정 10번 요청 완료", "/badges/neptune.png"),

	PLUTO("명왕성 탐사 완료", "수정 요청", "수정 요청 10개 반영 완료", "/badges/pluto.png"),

	SUN("태양 탐사 완료", "수정 요청", "수정 30번 요청 완료", "/badges/sun.png"),

	GALAXY("태양계", "수정 요청", "수정 50번 요청 완료", "/badges/galaxy.png"),

	ANDROMEDA("안드로메다 정복", "수정 요청", "수정 50번 반영 완료", "/badges/andromeda.png"),

	BLACKHOLE("블랙홀", "수정 요청", "수정 n번 반려 완료", "/badges/blackhole.png"),

	SPROUT("우주 새싹", "회원 가입", "회원 가입 완료", "/badges/sprout.png"),

	GUARD("우주 방범대", "신고", "신고 10회 완료", "/badges/guard.png");
	
	private final String title; // 홈페이지에서 사용자가 보는 배지 이름
	private final String eventCategory; // 배지 획득 조건
	private final String description; // 배지 설명
	private final String imgUrl; // 배지 이미지 URL

	// 생성자의 파라미터 순서에 따라 상기 enum value 순서가 결정됨.
	Badge(String title, String eventCategory, String description, String imgUrl) {
		this.title = title;
		this.eventCategory = eventCategory;
		this.description = description;
		this.imgUrl = imgUrl;
	}

}