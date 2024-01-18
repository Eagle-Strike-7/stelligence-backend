package goorm.eagle7.stelligence.common.login.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "from")
public class LoginTokenResponse {
	private String stelligenceSocialTypeToken;
}
