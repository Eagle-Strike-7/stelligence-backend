package goorm.eagle7.stelligence.common.login.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "of")
public class LoginTokens {

	private String accessToken;
	private String refreshToken;
	private String socialType;
}
