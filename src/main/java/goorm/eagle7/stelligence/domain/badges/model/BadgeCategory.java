package goorm.eagle7.stelligence.domain.badges.model;

import lombok.Getter;

@Getter
public enum BadgeCategory {
	WRITING("글"),
	EDIT_REQUEST("수정 요청"),
	MEMBER_JOIN("회원 가입"),
	REPORT("신고");

	private final String category;

	BadgeCategory(String category) {
		this.category = category;
	}

}
