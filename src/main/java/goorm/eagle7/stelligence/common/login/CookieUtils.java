package goorm.eagle7.stelligence.common.login;

import java.util.Optional;

import org.springframework.http.ResponseCookie;
import org.springframework.http.server.ServletServerHttpResponse;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 쿠키 Util
 * 	- filter, loginController에서 사용
 * 테스트 용이성을 위해 ServletServerHttpResponse 사용
 */

// TODO cookie 만료 시간 token과 동일하게 설정하기.
//todo: request, response 받지 않고, 생성자에서 받아서 사용하도록 변경
@Slf4j
public class CookieUtils {

	private static final String COOKIE_DOMAIN = "localhost";
	private static final String COOKIE_PATH = "/";
	private static final String COOKIE_SAME_SITE = "Lax"; // Naver는 Lax여야 로컬에서 동작
	private static final boolean COOKIE_SECURE = true;
	private static final boolean COOKIE_HTTP_ONLY = true;
	private static final String HEADER_NAME = "Set-Cookie"; // 2주

	private CookieUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static Optional<Cookie> getCookieFromCookies(HttpServletRequest request, String cookieName) {
		log.debug("cookiename = {}", cookieName);
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(cookieName)) {
					log.debug("cookie = {}", cookie);
					return Optional.of(cookie);
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * ResponseCookie 생성 후 response에 쿠키 추가
	 * ServletServerHttpResponse 사용
	 * @param response HttpServletResponse
	 * @param name 쿠키 이름
	 * @param content 쿠키 값
	 */
	public static void addCookie(HttpServletResponse response, String name, String content, String maxAgeStr) {

		// 테스트 용이성을 위해 ServletServerHttpResponse 사용
		try (ServletServerHttpResponse servletServerHttpResponse = new ServletServerHttpResponse(response)) {

			int maxAge = Integer.parseInt(maxAgeStr);
			ResponseCookie responseCookie = ResponseCookie.from(name, content)
				.httpOnly(COOKIE_HTTP_ONLY) // XSS 방지
				.maxAge(maxAge) //
				// .domain(COOKIE_DOMAIN) // localhost
				.path(COOKIE_PATH)
				// .sameSite(COOKIE_SAME_SITE) // CSRF 방지
				// .secure(COOKIE_SECURE) // HTTPS
				.build();
			servletServerHttpResponse.getServletResponse().addHeader(HEADER_NAME, responseCookie.toString());
		}
	}

	public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
		try (ServletServerHttpResponse servletServerHttpResponse = new ServletServerHttpResponse(response)) {
			getCookieFromCookies(request, name).ifPresent(cookie -> {
				ResponseCookie responseCookie = ResponseCookie.from(name, "")
					.httpOnly(COOKIE_HTTP_ONLY) // XSS 방지
					.maxAge(0)
					// .domain(COOKIE_DOMAIN)
					.path(COOKIE_PATH)
					// .sameSite(COOKIE_SAME_SITE) // CSRF 방지
					// .secure(COOKIE_SECURE) // HTTPS
					.build();
				servletServerHttpResponse.getServletResponse().addHeader(HEADER_NAME, responseCookie.toString());
			});

		}
	}
}
