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
public class MemberProfileResponse {

	private String nickname;
	private String email;
	private String profileImgUrl;
	private SocialType socialType;

	public static MemberProfileResponse from(Member member) {
		return new MemberProfileResponse(
			member.getNickname(),
			member.getEmail(),
			member.getImageUrl(),
			member.getSocialType()
		);
	}
}
