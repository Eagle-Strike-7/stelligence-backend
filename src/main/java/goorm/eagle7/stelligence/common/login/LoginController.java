package goorm.eagle7.stelligence.common.login;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import goorm.eagle7.stelligence.api.ApiResponse;
import goorm.eagle7.stelligence.common.login.dto.DevLoginRequest;
import goorm.eagle7.stelligence.common.login.dto.LoginTokenResponse;
import goorm.eagle7.stelligence.common.login.dto.LoginTokens;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api") // TODO /auth
public class LoginController {

	private final LoginService loginService;

	@Value("${jwt.accessToken.name}")
	private String accessTokenCookieName;

	@Value("${jwt.refreshToken.name}")
	private String refreshTokenCookieName;

	/**
	 * 로그인 혹은 회원 가입
	 * @param devLoginRequest 닉네임
	 * @param response response cookie에 token 저장
	 * @return socialType 토큰
	 */
	@PostMapping("/login")
	public ApiResponse<LoginTokenResponse> login(@RequestBody DevLoginRequest devLoginRequest,
		HttpServletResponse response) {

		// 로그인 혹은 회원 가입
		LoginTokens loginTokens = loginService.login(devLoginRequest);

		// cookie에 access 토큰, refreshToken 저장
		CookieUtils.addCookie(response, accessTokenCookieName, loginTokens.getAccessToken());
		CookieUtils.addCookie(response, refreshTokenCookieName, loginTokens.getRefreshToken());

		return ApiResponse.ok(LoginTokenResponse.of(loginTokens.getSocialType()));
	}
}
