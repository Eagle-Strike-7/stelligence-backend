package goorm.eagle7.stelligence.common.auth.filter;

import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.common.auth.filter.pathmatch.CustomRequestMatcher;
import goorm.eagle7.stelligence.common.auth.jwt.JwtTokenReissueService;
import goorm.eagle7.stelligence.common.auth.jwt.JwtTokenService;
import goorm.eagle7.stelligence.common.util.CookieType;
import goorm.eagle7.stelligence.common.util.CookieUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class AuthFilterTest {

	@Mock
	private  JwtTokenService jwtTokenService;
	@Mock
	private  JwtTokenReissueService jwtTokenReissueService;
	@Mock
	private  CustomRequestMatcher customRequestMatcher;
	@Mock
	private  CookieUtils cookieUtils;

	@InjectMocks
	private AuthFilter authFilter;

	private static final String ERROR_MESSAGE = "유효하지 않은 사용자입니다.";

	@Test
@DisplayName("[성공] 로그인 정상 진행 - doFilterInternal")
void doFilterInternal() throws Exception {
    // given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain filterChain = mock(FilterChain.class);
    Cookie accessCookie = new Cookie("ACCESS-TOKEN", "access");
    Cookie refreshCookie = new Cookie("REFRESH-TOKEN", "refresh");

    when(request.getMethod()).thenReturn("GET");
    when(request.getRequestURI()).thenReturn("/api/documents");
    when(customRequestMatcher.matches(request)).thenReturn(false);
    when(cookieUtils.getCookieFromRequest(CookieType.ACCESS_TOKEN)).thenReturn(Optional.of(accessCookie));

    // when
    authFilter.doFilterInternal(request, response, filterChain);

    // then
    verify(filterChain, times(1)).doFilter(request, response);

}
	// request가 없는 경우

	// 액세스 쿠키가 없는 경우

	// 토큰 만료된 경우

	// 토큰 재발급 성공한 경우

	// 토큰 재발급 실패한 경우

	// authenication 저장 성공


}