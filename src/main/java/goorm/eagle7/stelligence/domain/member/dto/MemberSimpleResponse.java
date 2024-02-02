package goorm.eagle7.stelligence.domain.member.dto;

import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "of")
public class MemberSimpleResponse {
	private Long memberId;
	private String nickname;
	private String profileImgUrl;

	public static MemberSimpleResponse from(Member member) {
		return new MemberSimpleResponse(member.getId(), member.getNickname(), member.getImageUrl());
	}
}
