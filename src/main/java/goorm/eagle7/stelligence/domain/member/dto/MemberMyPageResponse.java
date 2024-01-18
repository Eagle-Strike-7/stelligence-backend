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

	private String name;
	private String nickname;
	private String email;
	private String imageUrl;
	private SocialType socialType;

	// TODO private long contributes;
	// TODO badges
	// TODO bookmarks

	public static MemberMyPageResponse from(Member member) {
		return new MemberMyPageResponse(
			member.getName(),
			member.getNickname(),
			member.getEmail(),
			member.getImageUrl(),
			member.getSocialType()
		);
	}
}
