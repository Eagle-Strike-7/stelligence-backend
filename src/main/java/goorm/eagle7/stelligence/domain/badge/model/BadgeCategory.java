package goorm.eagle7.stelligence.domain.badge.model;

import lombok.Getter;

@Getter
public enum BadgeCategory {
	WRITING("글"),
	CONTRIBUTE_ALL("수정 요청"),
	CONTRIBUTE_MERGED("수정 요청 반영"),
	CONTRIBUTE_REJECTED("수정 요청 반려"),
	MEMBER_JOIN("회원 가입"),
	REPORT("신고");

	private final String label;

	BadgeCategory(String label) {
		this.label = label;
	}

}
