package goorm.eagle7.stelligence.common.login.dto;

import goorm.eagle7.stelligence.domain.member.model.SocialType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "of")
public class LoginOAuth2Request {

	private String name;
	private String nickname;
	private String email;
	private String imageUrl;
	private String socialId;
	private SocialType socialType;

}
