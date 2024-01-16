package goorm.eagle7.stelligence.common.login;

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
	private static final String ACCESS_TOKEN_COOKIE = "StelligenceAccessToken";
	private static final String REFRESH_TOKEN_COOKIE = "StelligenceRefreshToken";
	private static final String SOCIAL_TYPE_TOKEN_COOKIE = "StelligenceSocialTypeToken";
	private static final int MAX_AGE_COOKIE = 60 * 60 * 24 * 30;
	private static final String SET_COOKIE = "Set-Cookie";

	@PostMapping("/login")
	public ApiResponse<Void> login(@RequestBody LoginRequest loginRequest,
		HttpServletResponse response) {

		LoginTokensResponse loginTokensResponse = loginService.login(loginRequest);
		// TODO 테스트 용이성을 위해 ServletServerHttpResponse 사용
		ServletServerHttpResponse servletServerHttpResponse = new ServletServerHttpResponse(response);

		addCookie(servletServerHttpResponse, ACCESS_TOKEN_COOKIE, loginTokensResponse.getAccessToken());
		addCookie(servletServerHttpResponse, REFRESH_TOKEN_COOKIE, loginTokensResponse.getRefreshToken());
		addCookie(servletServerHttpResponse, SOCIAL_TYPE_TOKEN_COOKIE, loginTokensResponse.getSocialType());

		return ApiResponse.ok();
	}

	private static void addCookie(ServletServerHttpResponse response, String name, String content) {
		ResponseCookie responseCookie = ResponseCookie.from(name,
				content)
			.httpOnly(true)
			.maxAge(MAX_AGE_COOKIE)
			.path("/")
			// .sameSite("Strict") // CSRF 방지
			// .secure(true) // HTTPS
			.build();
		response.getServletResponse().addHeader(SET_COOKIE, responseCookie.toString());
	}

}
