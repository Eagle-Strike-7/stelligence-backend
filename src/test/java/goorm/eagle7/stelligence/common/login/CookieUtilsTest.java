package goorm.eagle7.stelligence.common.login;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import goorm.eagle7.stelligence.common.util.CookieType;
import goorm.eagle7.stelligence.common.util.CookieUtils;
import goorm.eagle7.stelligence.common.util.RequestScopeUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CookieUtilsTest {

	@InjectMocks
	private CookieUtils cookieUtils;

	private HttpServletRequest request;
	private HttpServletResponse response;

	private String testAccessTokenCookieName;
	private String refreshTokenCookieName;
	private String accessToken;
	private String refreshToken;
	private Long accessCookieMaxAge;
	private Long refreshCookieMaxAge;
	private String cookieDomain;
	private String cookiePath;
	private String cookieSameSite;
	private boolean cookieSecure;
	private boolean cookieHttpOnly;
	private String headerName;

	@BeforeEach
	void setUp() {

		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		testAccessTokenCookieName = "accessTokenCookie";
		refreshTokenCookieName = "refreshTokenCookie";
		accessToken = "accessToken";
		refreshToken = "refreshToken";
		accessCookieMaxAge = 1000L;
		refreshCookieMaxAge = 2000L;
		cookieDomain = "cookieDomain";
		cookiePath = "cookiePath";
		cookieSameSite = "Lax";
		cookieSecure = true;
		cookieHttpOnly = true;
		headerName = "Set-Cookie";

		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request, response));

		// ReflectionTestUtils를 사용하여 필드 값 설정
		ReflectionTestUtils.setField(cookieUtils, "accessTokenCookieName", testAccessTokenCookieName);
		ReflectionTestUtils.setField(cookieUtils, "refreshTokenCookieName", refreshTokenCookieName);
		ReflectionTestUtils.setField(cookieUtils, "accessCookieMaxAge", accessCookieMaxAge);
		ReflectionTestUtils.setField(cookieUtils, "refreshCookieMaxAge", refreshCookieMaxAge);
		ReflectionTestUtils.setField(cookieUtils, "cookieDomain", cookieDomain);
		ReflectionTestUtils.setField(cookieUtils, "cookiePath", cookiePath);
		ReflectionTestUtils.setField(cookieUtils, "cookieSameSite", cookieSameSite);
		ReflectionTestUtils.setField(cookieUtils, "cookieSecure", cookieSecure);
		ReflectionTestUtils.setField(cookieUtils, "cookieHttpOnly", cookieHttpOnly);
		ReflectionTestUtils.setField(cookieUtils, "headerName", headerName);

	}

	@Test
	@DisplayName("[성공] 쿠키 조회 - getCookieFromRequest")
	void getCookieFromRequest() {

		try (MockedStatic<RequestScopeUtils> mocked = mockStatic(RequestScopeUtils.class)) {

			// Arrange: Set up your test
			Cookie cookie = new Cookie(testAccessTokenCookieName, accessToken);
			when(request.getCookies()).thenReturn(new Cookie[]{cookie});
			mocked.when(RequestScopeUtils::getHttpServletRequest).thenReturn(request);

			// Act: Call the method you want to test
			Optional<Cookie> actualCookie = cookieUtils.getCookieFromRequest(CookieType.ACCESS_TOKEN);

			// Assert: Check the result
			assertThat(actualCookie).isPresent();
			assertThat(actualCookie.get().getName()).isEqualTo(testAccessTokenCookieName);
			assertThat(actualCookie.get().getValue()).isEqualTo(accessToken);

		}

	}

	@Test
	@DisplayName("[예외] 쿠키 조회, cookies(모든 쿠키) 없음 - getCookieFromRequest")
	void getCookieFromRequestNoCookie() {

		try (MockedStatic<RequestScopeUtils> mocked = mockStatic(RequestScopeUtils.class)) {

			// given - cookies 없음
			mocked.when(RequestScopeUtils::getHttpServletRequest).thenReturn(request);

			// when
			Optional<Cookie> actualCookie = cookieUtils.getCookieFromRequest(CookieType.ACCESS_TOKEN);

			// then
			assertThat(actualCookie).isEmpty();

		}

	}

	@Test
	@DisplayName("[예외] 쿠키 조회, 특정 쿠키 없음 - getCookieFromRequest")
	void getCookieFromRequestNoSpecificCookie() {

		try (MockedStatic<RequestScopeUtils> mocked = mockStatic(RequestScopeUtils.class)) {

			// given
			Cookie cookie = new Cookie(testAccessTokenCookieName, accessToken);
			when(request.getCookies()).thenReturn(new Cookie[]{cookie});
			mocked.when(RequestScopeUtils::getHttpServletRequest).thenReturn(request);

			// when
			Optional<Cookie> actualCookie = cookieUtils.getCookieFromRequest(CookieType.REFRESH_TOKEN);

			// then
			assertThat(actualCookie).isEmpty();
			verify(request, times(1)).getCookies();

		}

	}

	@Test
	@DisplayName("[성공] accessToken 쿠키 추가 - addCookieBy")
	void addCookieBy() {

		// given
		try (MockedStatic<RequestScopeUtils> mocked = mockStatic(RequestScopeUtils.class)) {

			// given
			ResponseCookie responseCookie = ResponseCookie.from(testAccessTokenCookieName, accessToken)
				.maxAge(accessCookieMaxAge)
				.domain(cookieDomain)
				.path(cookiePath)
				.sameSite(cookieSameSite)
				.secure(cookieSecure)
				.httpOnly(cookieHttpOnly)
				.build();
			mocked.when(RequestScopeUtils::getHttpServletResponse).thenReturn(response);
			when(response.getHeader(headerName)).thenReturn(responseCookie.toString());

			// when
			cookieUtils.addCookieBy(CookieType.ACCESS_TOKEN, accessToken);

			// then
			assertThat(response.getHeader(headerName)).startsWith(testAccessTokenCookieName);

			log.info("response.getCookie(testAccessTokenCookieName).getValue(): {}",
				response.getHeader(testAccessTokenCookieName));
			log.info("responseCookie.toString(): {}", responseCookie);

		}

	}

	@Test
	@DisplayName("[성공] 쿠키 삭제 - deleteCookieBy")
	void deleteCookieBy() {

		// given
		try (MockedStatic<RequestScopeUtils> mocked = mockStatic(RequestScopeUtils.class)) {

			// request에 쿠키 추가
			ResponseCookie responseCookie = ResponseCookie.from(testAccessTokenCookieName, "")
				.maxAge(0)
				.domain(cookieDomain)
				.path(cookiePath)
				.sameSite(cookieSameSite)
				.secure(cookieSecure)
				.httpOnly(cookieHttpOnly)
				.build();
			mocked.when(RequestScopeUtils::getHttpServletRequest).thenReturn(request);
			mocked.when(RequestScopeUtils::getHttpServletResponse).thenReturn(response);

			// when
			cookieUtils.deleteCookieBy(CookieType.ACCESS_TOKEN);

			// then - 쿠키 삭제 시 null 반환
			assertThat(response.getHeader(testAccessTokenCookieName)).isNull();

			log.info("response.getCookie(testAccessTokenCookieName): {}",
				response.getHeader(testAccessTokenCookieName));
			log.info("responseCookie.toString(): {}", responseCookie);

		}

	}

}