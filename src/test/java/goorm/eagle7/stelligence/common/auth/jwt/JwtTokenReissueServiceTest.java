package goorm.eagle7.stelligence.common.auth.jwt;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import goorm.eagle7.stelligence.common.login.dto.LoginTokenInfo;
import goorm.eagle7.stelligence.common.util.CookieUtils;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@ExtendWith(MockitoExtension.class)
class JwtTokenReissueServiceTest {

	@Mock
	private JwtTokenService jwtTokenService;
	@Mock
	private JwtTokenProvider jwtTokenProvider;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private CookieUtils cookieUtils;

	@InjectMocks
	private JwtTokenReissueService jwtTokenReissueService;

	private static final String ERROR_MESSAGE = "유효하지 않은 사용자입니다.";

	@Test
	@DisplayName("[성공] refreshToken 검증해 accessToken 재발급 - reissueAccessToken")
	void reissueAccessToken() {

		// given
		String refreshToken = "refreshToken";
		Claims claims = Jwts.claims().subject("1").build();
		Member member = member(1L, "nickname");
		member.updateRefreshToken(refreshToken);

		// refreshToken 검증 및 member 조회
		when(jwtTokenService.validateTokenOrThrows(refreshToken)).thenReturn(Optional.of(claims));
		when(jwtTokenService.getMemberId(claims)).thenReturn(1L);
		when(memberRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(member));

		// token 유효성 검증 validateTokenEquality

		// accessToken 재발급 및 쿠키에 추가 후 accessToken 반환
		when(jwtTokenProvider.createAccessToken(1L)).thenReturn("accessTokenDiff");
		when(jwtTokenProvider.createRefreshToken(1L)).thenReturn("refreshTokenDiff");

		// when
		LoginTokenInfo loginTokenInfo = jwtTokenReissueService.reissueAccessToken(refreshToken);

		// then
		assertThat(loginTokenInfo.getAccessToken()).isEqualTo("accessTokenDiff");
		assertThat(loginTokenInfo.getRefreshToken()).isEqualTo("refreshTokenDiff");
		verify(jwtTokenService, times(1)).validateTokenOrThrows(refreshToken);
		verify(jwtTokenService, times(1)).getMemberId(claims);
		verify(memberRepository, times(1)).findByIdAndActiveTrue(1L);
		verify(jwtTokenProvider, times(1)).createAccessToken(1L);
		verify(jwtTokenProvider, times(1)).createRefreshToken(1L);

	}

	@Test
	@DisplayName("[실패] refreshToken 만료 - reissueAccessToken")
	void reissueAccessTokenThrowsExpiredJwtException() {

		// given
		String expiredRefreshToken = "expiredRefreshToken";
		when(jwtTokenService.validateTokenOrThrows(expiredRefreshToken)).thenReturn(Optional.empty());

		// when, then
		assertThatThrownBy(() -> jwtTokenReissueService.reissueAccessToken(expiredRefreshToken))
			.isInstanceOf(UsernameNotFoundException.class)
			.hasMessage(ERROR_MESSAGE);

		verify(jwtTokenService, times(1)).validateTokenOrThrows(expiredRefreshToken);
		verify(jwtTokenService, never()).getMemberId(any());
		verify(memberRepository, never()).findByIdAndActiveTrue(any());
		verify(jwtTokenProvider, never()).createAccessToken(any());
		verify(jwtTokenProvider, never()).createRefreshToken(any());

	}

	@Test
	@DisplayName("[실패] memberId에 해당하는 회원이 없음 - reissueAccessToken")
	void reissueAccessTokenThrowsUsernameNotFoundException() {

		// given
		String refreshToken = "refreshToken";
		Claims claims = Jwts.claims().subject("1").build();
		when(jwtTokenService.validateTokenOrThrows(refreshToken)).thenReturn(Optional.of(claims));
		when(jwtTokenService.getMemberId(claims)).thenReturn(1L);
		when(memberRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());

		// when, then
		assertThatThrownBy(() -> jwtTokenReissueService.reissueAccessToken(refreshToken))
			.isInstanceOf(UsernameNotFoundException.class)
			.hasMessage(ERROR_MESSAGE);

		verify(jwtTokenService, times(1)).validateTokenOrThrows(refreshToken);
		verify(jwtTokenService, times(1)).getMemberId(claims);
		verify(memberRepository, times(1)).findByIdAndActiveTrue(1L);
		verify(jwtTokenProvider, never()).createAccessToken(any());
		verify(jwtTokenProvider, never()).createRefreshToken(any());

	}

	@Test
	@DisplayName("[실패] refreshToken이 DB에 저장된 refreshToken과 일치하지 않음 - reissueAccessToken")
	void reissueAccessTokenThrowsUsernameNotFoundException2() {

		// given
		String refreshToken = "refreshTokenDiff";
		Claims claims = Jwts.claims().subject("1").build();
		Member member = member(1L, "nickname");
		member.updateRefreshToken("refreshToken");

		when(jwtTokenService.validateTokenOrThrows(refreshToken)).thenReturn(Optional.of(claims));
		when(jwtTokenService.getMemberId(claims)).thenReturn(1L);
		when(memberRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(member));

		// when, then
		assertThatThrownBy(() -> jwtTokenReissueService.reissueAccessToken(refreshToken))
			.isInstanceOf(UsernameNotFoundException.class)
			.hasMessage(ERROR_MESSAGE);

		verify(jwtTokenService, times(1)).validateTokenOrThrows(refreshToken);
		verify(jwtTokenService, times(1)).getMemberId(claims);
		verify(memberRepository, times(1)).findByIdAndActiveTrue(1L);
		verify(jwtTokenProvider, never()).createAccessToken(any());
		verify(jwtTokenProvider, never()).createRefreshToken(any());

	}

}