package goorm.eagle7.stelligence.domain.member.dto;

import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.member.model.SocialType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberMyPageResponse {

	private String nickname;
	private String email;
	private String profileUrl;
	private SocialType socialType;

	public static MemberMyPageResponse from(Member member) {
		return new MemberMyPageResponse(
			member.getNickname(),
			member.getEmail(),
			member.getImageUrl(),
			member.getSocialType()
		);
	}
}
