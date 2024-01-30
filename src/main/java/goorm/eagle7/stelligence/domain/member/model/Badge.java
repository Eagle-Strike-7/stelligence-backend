package goorm.eagle7.stelligence.domain.member.model;

public enum Badge {

	ASTRONAUT("우주 여행자"),
	MOON("달 탐사 완료"),
	MERCURY("수성 탐사 완료"),
	VENUS( "금성 탐사 완료"),
	MARS("화성 탐사 완료"),
	JUPITER("목성 탐사 완료"),
	SATURN("토성 탐사 완료"),
	URANUS("천왕성 탐사 완료"),
	NEPTUNE("해왕성 탐사 완료"),
	PLUTO("명왕성 탐사 완료"),
	SUN("태양 탐사 완료"),
	GALAXY("태양계"),
	ANDROMEDA("안드로메다 정복"),
	BLACKHOLE("블랙홀"),
	SPROUT("우주 새싹"),
	GUARD("우주 방범대");

	private final String title; // 홈페이지에서 사용자가 보는 뱃지 이름

	Badge(String title) {
		this.title = title;
	}

}
