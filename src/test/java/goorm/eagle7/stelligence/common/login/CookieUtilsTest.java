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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.Cookie;

@ExtendWith(MockitoExtension.class)
class CookieUtilsTest {

	@InjectMocks
	private CookieUtils cookieUtils;

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;

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

		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
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
		ReflectionTestUtils.setField(cookieUtils, "refreshTokenCookieName", "refreshTokenCookie");
		ReflectionTestUtils.setField(cookieUtils, "accessCookieMaxAge", 1000L);
		ReflectionTestUtils.setField(cookieUtils, "refreshCookieMaxAge", 2000L);
		ReflectionTestUtils.setField(cookieUtils, "cookieDomain", "cookieDomain");
		ReflectionTestUtils.setField(cookieUtils, "cookiePath", "cookiePath");
		ReflectionTestUtils.setField(cookieUtils, "cookieSameSite", "Lax");
		ReflectionTestUtils.setField(cookieUtils, "cookieSecure", true);
		ReflectionTestUtils.setField(cookieUtils, "cookieHttpOnly", true);


	}

	@Test
	@DisplayName("[성공] 쿠키 조회 - getCookieFromRequest")
	void getCookieFromRequest() {

		try (MockedStatic<RequestScopeUtils> mocked = mockStatic(RequestScopeUtils.class)) {

			// Arrange: Set up your test
			Cookie cookie = new Cookie(testAccessTokenCookieName, accessToken);
			request.setCookies(cookie);
			mocked.when(RequestScopeUtils::getHttpServletRequest).thenReturn(request);

			// Act: Call the method you want to test
			Optional<Cookie> actualCookie = cookieUtils.getCookieFromRequest(CookieType.ACCESS_TOKEN);

			// Assert: Check the result
			assertThat(actualCookie).isPresent();
			assertThat(actualCookie.get().getName()).isEqualTo(testAccessTokenCookieName);
			assertThat(actualCookie.get().getValue()).isEqualTo(accessToken);
		}

	}
	//
	// @Test
	// void addCookieBy() {
	//
	// 	// given
	//
	// 	ResponseCookie responseCookie = ResponseCookie.from(accessTokenCookieName, accessToken)
	// 		.maxAge(accessCookieMaxAge)
	// 		.domain(cookieDomain)
	// 		.path(cookiePath)
	// 		.sameSite(cookieSameSite)
	// 		.secure(cookieSecure)
	// 		.httpOnly(cookieHttpOnly)
	// 		.build();
	//
	// }
	//
	// @Test
	// void deleteCookieBy() {
	// }
}