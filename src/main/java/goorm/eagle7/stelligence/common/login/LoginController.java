package goorm.eagle7.stelligence.common.login;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import goorm.eagle7.stelligence.api.ApiResponse;
import goorm.eagle7.stelligence.common.login.dto.LoginRequest;
import goorm.eagle7.stelligence.common.login.dto.LoginTokensResponse;
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

	@Value("${jwt.socialTypeToken.name}")
	private String socialTypeTokenCookieName;

	@Value("${http.cookie.maxAge}")
	private int maxAgeCookie; // 14일

	@Value("${http.cookie.field}")
	private String setCookie;

	@PostMapping("/login")
	public ApiResponse<Void> login(@RequestBody LoginRequest loginRequest,
		HttpServletResponse response) {

		// 로그인 혹은 회원 가입
		LoginTokensResponse loginTokensResponse = loginService.login(loginRequest);
		// cookie에 토큰 저장
		addCookies(response, loginTokensResponse);

		return ApiResponse.ok();
	}

	private void addCookies(HttpServletResponse response, LoginTokensResponse loginTokensResponse) {

		// TODO 테스트 용이성을 위해 ServletServerHttpResponse 사용
		ServletServerHttpResponse servletServerHttpResponse = new ServletServerHttpResponse(response);

		addCookie(servletServerHttpResponse, accessTokenCookieName, loginTokensResponse.getAccessToken());
		addCookie(servletServerHttpResponse, refreshTokenCookieName, loginTokensResponse.getRefreshToken());
		addCookie(servletServerHttpResponse, socialTypeTokenCookieName, loginTokensResponse.getSocialType());

	}

	private void addCookie(ServletServerHttpResponse response, String name, String content) {
		ResponseCookie responseCookie = ResponseCookie.from(name,
				content)
			.httpOnly(true)
			.maxAge(maxAgeCookie)
			.path("/")
			// .sameSite("Strict") // CSRF 방지
			// .secure(true) // HTTPS
			.build();
		response.getServletResponse().addHeader(setCookie, responseCookie.toString());
	}

}
