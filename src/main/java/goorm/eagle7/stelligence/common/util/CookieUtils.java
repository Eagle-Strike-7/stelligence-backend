package goorm.eagle7.stelligence.common.util;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * <h2>쿠키 Util</h2>
 * <p>- 쿠키 생성, 삭제, 조회</p>
 * <p>- sameSite 정책 고려해 responseCookie 이용</p>
 */
@Component
@RequiredArgsConstructor // final 필드가 없지만, 우선 선언. TODO 적절한지 확인
public class CookieUtils {

	@Value("${http.cookie.accessToken.name}")
	private String accessTokenCookieName;
	@Value("${http.cookie.refreshToken.name}")
	private String refreshTokenCookieName;
	@Value("${http.cookie.accessToken.maxAge}")
	private Long accessCookieMaxAge;
	@Value("${http.cookie.refreshToken.maxAge}")
	private Long refreshCookieMaxAge;
	@Value("${http.cookie.domain}")
	private String cookieDomain;
	@Value("${http.cookie.path}")
	private String cookiePath;
	@Value("${http.cookie.sameSite}")
	private String cookieSameSite; // Naver는 Lax여야 로컬에서 동작
	@Value("${http.cookie.secure}")
	private boolean cookieSecure;
	@Value("${http.cookie.httpOnly}")
	private boolean cookieHttpOnly;
	@Value("${http.cookie.headerName}")
	private String headerName;

	/**
	 * <h2>쿠키 조회</h2>
	 * <p>- cookieType으로 구분해 내부에서 처리</p>
	 * <p>- 쿠키가 없는 경우, 상황에 따라 exception 다를 수 있어 optional로 진행</p>
	 * @param cookieType 쿠키 타입, accessToken, refreshToken
	 * @return 쿠키, 없으면 Optional.empty()
	 * @throws IllegalStateException request가 없는 경우
	 */
	public Optional<Cookie> getCookieFromRequest(CookieType cookieType) {
		return switch (cookieType) {
			case ACCESS_TOKEN -> getCookie(accessTokenCookieName);
			case REFRESH_TOKEN -> getCookie(refreshTokenCookieName);
		};
	}

	/**
	 * <h2>쿠키 조회</h2>
	 * <p>쿠키 이름으로 조회</p>
	 * @param cookieName 쿠키 이름
	 * @return cookie List 혹은 해당하는 cookie가 없으면 Optional.empty()
	 * @throws IllegalStateException request가 없는 경우
	 */
	private Optional<Cookie> getCookie(String cookieName) {

		HttpServletRequest request = RequestScopeUtils.getHttpServletRequest();

		Cookie[] cookies = request.getCookies();

		// 쿠키가 없는 경우
		if (cookies == null) {
			return Optional.empty();
		}

		// 쿠키 이름으로 필터링, 일치하는 쿠키 아무거나 반환, 없으면 empty 반환
		return Arrays.stream(request.getCookies())
			.filter(cookie -> cookie.getName().equals(cookieName))
			.findAny();
	}

	/**
	 * <h2>쿠키 추가</h2>
	 * <p>- cookieType으로 구분해 내부에서 처리</p>
	 * @param cookieType 쿠키 타입, accessToken, refreshToken
	 * @param value 쿠키 값 - 현재는 토큰 값
	 * @throws IllegalStateException response가 없는 경우
	 */
	public void addCookieBy(CookieType cookieType, String value) {

		switch (cookieType) {
			case ACCESS_TOKEN -> addCookie(accessTokenCookieName, value, accessCookieMaxAge);
			case REFRESH_TOKEN -> addCookie(refreshTokenCookieName, value, refreshCookieMaxAge);
		}

	}

	/**
	 * <h2>쿠키 추가</h2>
	 * <p>- 쿠키 이름, 값, 만료 시간으로 추가</p>
	 * @param name 쿠키 이름
	 * @param value 쿠키 값
	 * @param maxAge 쿠키 만료 시간
	 * @throws IllegalStateException response가 없는 경우
	 */
	private void addCookie(String name, String value, long maxAge) {

		HttpServletResponse response = RequestScopeUtils.getHttpServletResponse();

		ResponseCookie responseCookie = ResponseCookie.from(name, value)
			.httpOnly(cookieHttpOnly) // XSS 방지
			.maxAge(maxAge) //
			.domain(cookieDomain)
			.path(cookiePath)
			.sameSite(cookieSameSite) // CSRF 방지
			.secure(cookieSecure) // HTTPS
			.build();

		response.addHeader(headerName, responseCookie.toString());

	}

	/**
	 * <h2>쿠키 삭제</h2>
	 * <p>- cookieType으로 구분해 내부에서 처리</p>
	 * @param cookieType 쿠키 타입, accessToken, refreshToken
	 * @throws IllegalStateException response가 없는 경우
	 */
	public void deleteCookieBy(CookieType cookieType) {
		getCookieFromRequest(cookieType).ifPresent(cookie -> {
			switch (cookieType) {
				case ACCESS_TOKEN -> deleteCookie(accessTokenCookieName);
				case REFRESH_TOKEN -> deleteCookie(refreshTokenCookieName);
			}
		});
	}

	/**
	 * <h2>쿠키 삭제</h2>
	 * <p>- 쿠키 이름으로 삭제</p>
	 * @param cookieName 쿠키 이름
	 * @throws IllegalStateException response가 없는 경우
	 */
	private void deleteCookie(String cookieName) {

		HttpServletResponse response = RequestScopeUtils.getHttpServletResponse();

		ResponseCookie responseCookie = ResponseCookie.from(cookieName, "")
			.httpOnly(cookieHttpOnly) // XSS 방지
			.maxAge(0)
			.domain(cookieDomain)
			.path(cookiePath)
			.sameSite(cookieSameSite) // CSRF 방지
			.secure(cookieSecure) // HTTPS
			.build();

		response.addHeader(headerName, responseCookie.toString());

	}

}
