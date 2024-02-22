package goorm.eagle7.stelligence.common.auth.filter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.common.auth.filter.pathmatch.CustomRequestMatcher;
import goorm.eagle7.stelligence.common.auth.jwt.JwtTokenReissueService;
import goorm.eagle7.stelligence.common.auth.jwt.JwtTokenService;
import goorm.eagle7.stelligence.common.util.CookieUtils;

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
	void doFilterInternal() {
		// given
		// when
		// then
	}

	// request가 없는 경우

	// 액세스 쿠키가 없는 경우

	// 토큰 만료된 경우

	// 토큰 재발급 성공한 경우

	// 토큰 재발급 실패한 경우

	// authenication 저장 성공


}