package goorm.eagle7.stelligence.common.login;

import org.springframework.http.ResponseCookie;
import org.springframework.http.server.ServletServerHttpResponse;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 쿠키 Util
 * 	- filter, loginController에서 사용
 * 테스트 용이성을 위해 ServletServerHttpResponse 사용
 * TODO MaxAge, Set-Cookie 하드 코딩 개선 필요.
 *
 */
public class CookieUtils {

	private CookieUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * ResponseCookie 생성 후 response에 쿠키 추가
	 * ServletServerHttpResponse 사용
	 * @param response HttpServletResponse
	 * @param name 쿠키 이름
	 * @param content 쿠키 값
	 */
	public static void addCookie(HttpServletResponse response, String name, String content) {

		// TODO 테스트 용이성을 위해 ServletServerHttpResponse 사용
		try (ServletServerHttpResponse servletServerHttpResponse = new ServletServerHttpResponse(response)) {

			ResponseCookie responseCookie = ResponseCookie.from(name, content)
				.httpOnly(true)
				.maxAge(1209600)
				.path("/")
				// .sameSite("Strict") // CSRF 방지
				// .secure(true) // HTTPS
				.build();
			servletServerHttpResponse.getServletResponse().addHeader("Set-Cookie", responseCookie.toString());
		}

	}
}
