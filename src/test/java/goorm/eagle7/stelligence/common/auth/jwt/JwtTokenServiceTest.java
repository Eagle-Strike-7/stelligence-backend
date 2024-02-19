package goorm.eagle7.stelligence.common.auth.jwt;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.JwtException;

import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
import goorm.eagle7.stelligence.domain.member.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;

@ExtendWith(MockitoExtension.class)
class JwtTokenServiceTest {

	@Mock
	private JwtProperties jwtProperties;
	@Mock
	private JwtTokenParser jwtTokenParser;
	@Mock
	private JwtTokenValidator jwtTokenValidator;

	@InjectMocks
	private JwtTokenService jwtTokenService;

	private String cookieName = "StelligenceAccessToken";
	private final Long memberId = 1L;

	@Test
	@DisplayName("[성공] 토큰에서 memberId 추출 - getMemberId")
		// TODO claims 만료인 경우 확인 필요
	void getMemberId() {

		// given
		Long expectedMemberId = 1L;
		Claims claims = Jwts.claims().subject("1").build();
		when(jwtTokenParser.getSubject(claims)).thenReturn("1");

		// when
		Long actualMemberId = jwtTokenService.getMemberId(claims);

		// then
		assertThat(actualMemberId).isEqualTo(expectedMemberId);

	}

	@Test
	@DisplayName("[성공] 유효, 토큰 유효성 통합 검증 - isTokenValidated")
	void isTokenValidated() {

		// given

		// when
		boolean tokenValidated = jwtTokenService.isTokenValidated("accessToken");

		// then - void 반환
		assertThat(tokenValidated).isTrue();

	}

	@Test
	@DisplayName("[처리] UsernameNotFoundException 처리, 토큰 유효성 통합 검증 - isTokenValidated")
	void isTokenValidatedThrows() {

		// given
		JwtTokenService spyJwtTokenService = spy(jwtTokenService);
		when(spyJwtTokenService.validateTokenOrThrows(anyString())).thenThrow(UsernameNotFoundException.class);

		// when
		boolean tokenValidated = spyJwtTokenService.isTokenValidated("anyToken");

		// then - void 반환
		assertThat(tokenValidated).isFalse();

	}

	@Test
	@DisplayName("[처리] JwtException 처리, 토큰 유효성 통합 검증 - isTokenValidated")
	void isTokenValidatedThrowsJwtException() {

		// given
		JwtTokenService spyJwtTokenService = spy(jwtTokenService);
		when(spyJwtTokenService.validateTokenOrThrows(anyString())).thenThrow(JwtException.class);

		// when
		boolean tokenValidated = spyJwtTokenService.isTokenValidated("anyToken");

		// then - void 반환
		assertThat(tokenValidated).isFalse();

	}

	@Test
	@DisplayName("[처리] IllegalArgumentException 처리, 토큰 유효성 통합 검증 - isTokenValidated")
	void isTokenValidatedThrowsIllegalArgumentException() {

		// given
		JwtTokenService spyJwtTokenService = spy(new JwtTokenService(
			jwtProperties, jwtTokenParser, jwtTokenValidator
		));
		doThrow(IllegalArgumentException.class).when(spyJwtTokenService).validateTokenOrThrows(anyString());

		// when
		boolean tokenValidated = spyJwtTokenService.isTokenValidated("anyToken");

		// then - void 반환
		assertThat(tokenValidated).isFalse();

	}

	@Test
	@DisplayName("[성공] 토큰에서 Authentication 추출 - 유효")
	void makeAuthenticationFromTokenTrue() {

		// given
		JwtTokenService spyJwtTokenService = spy(jwtTokenService);
		Claims claims = Jwts.claims().subject(memberId.toString()).add("role", "USER").build();

		// Properties claims 세팅
		JwtProperties.Claims claimsProps = new JwtProperties.Claims();
		claimsProps.setRole("role");
		claimsProps.setValue("USER");

		when(jwtTokenParser.getClaims(anyString())).thenReturn(Optional.of(claims));
		doReturn(MemberInfo.of(memberId, Role.USER)).when(spyJwtTokenService).extractMemberInfo(claims);

		// when
		Authentication authentication = spyJwtTokenService.makeAuthenticationFrom("accessToken");

		// then
		assertThat(((User)authentication.getPrincipal()).getUsername()).isEqualTo(memberId.toString());

	}

	@Test
	@DisplayName("[예외] 유효하지 않은 토큰에서 Authentication 추출 - 유효하지 않음, throws")
	void makeAuthenticationFromTokenFalse() {

		// given

		// when

		// then
		assertThatThrownBy(() ->
			jwtTokenService.makeAuthenticationFrom(null))
			.isInstanceOf(UsernameNotFoundException.class);
	}

	@Test
	@DisplayName("[성공] 유효한 토큰에서 MemberInfo 추출 - extractMemberInfo")
	void extractMemberInfo() {

		// given
		Claims claims = Jwts.claims().subject("1").add("role", "USER").build();
		when(jwtTokenParser.getSubject(claims)).thenReturn("1");

		// Properties claims 세팅
		JwtProperties.Claims claimsProps = new JwtProperties.Claims();
		claimsProps.setRole("role");
		claimsProps.setValue("USER");

		when(jwtProperties.getClaims()).thenReturn(claimsProps);
		when(jwtTokenParser.getRole(claims, claimsProps.getRole())).thenReturn(Role.USER);

		// when
		MemberInfo memberInfo = jwtTokenService.extractMemberInfo(claims);

		// then
		assertThat(memberInfo.getId()).isEqualTo(memberId);
		assertThat(memberInfo.getRole()).isEqualTo(Role.USER);

	}

	@Test
	@DisplayName("[성공] 쿠키에서 토큰 추출 - getTokenFromCookie")
	void getTokenFromCookieTrue() {

		// given
		Cookie cookie = new Cookie(cookieName, "accessToken");

		// when
		String token = jwtTokenService.getTokenFromCookie(cookie);

		// then
		assertThat(token).isEqualTo("accessToken");

	}

	@Test
	@DisplayName("[성공] 쿠키에서 토큰 추출 시도 시 없는 경우 - getTokenFromCookie")
	void getTokenFromCookieFalse() {

		// given

		String tokenValueNull = null;
		String tokenValueEmpty = "";
		String tokenValueBlank = " ";

		Cookie cookieEmpty = new Cookie(cookieName, tokenValueEmpty);
		Cookie cookieBlank = new Cookie(cookieName, tokenValueBlank);

		// when
		String tokenNull = jwtTokenService.getTokenFromCookie(null);
		String tokenEmpty = jwtTokenService.getTokenFromCookie(cookieEmpty);
		String tokenBlank = jwtTokenService.getTokenFromCookie(cookieBlank);

		// then
		assertThat(tokenNull).isNull();
		assertThat(tokenEmpty).isEqualTo(tokenValueEmpty);
		assertThat(tokenBlank).isEqualTo(tokenValueBlank);

	}

}