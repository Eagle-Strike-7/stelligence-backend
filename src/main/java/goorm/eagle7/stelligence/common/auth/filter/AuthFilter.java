package goorm.eagle7.stelligence.common.auth.filter;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import goorm.eagle7.stelligence.common.auth.filter.pathmatch.CustomRequestMatcher;
import goorm.eagle7.stelligence.common.auth.jwt.JwtTokenReissueService;
import goorm.eagle7.stelligence.common.auth.jwt.JwtTokenService;
import goorm.eagle7.stelligence.common.login.CookieType;
import goorm.eagle7.stelligence.common.login.CookieUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

	private static final String ERROR_MESSAGE = "유효하지 않은 사용자입니다.";

	private final JwtTokenService jwtTokenService;
	private final JwtTokenReissueService jwtTokenReissueService;
	private final CustomRequestMatcher customRequestMatcher;
	private final CookieUtils cookieUtils;

	/**
	 * 토큰 검증이 필요한 리소스에 대해서만 검증 진행.
	 * 		-> 토큰 검증 X uri: ResourceMemoryRepository에서 가져온다.
	 * 			-> GET: /api/contributes, /api/documents, /api/comments, /api/debates, /login/oauth2/code/**, /oauth2/**
	 * 			-> POST: /api/login
	 * 		-> 토큰 검증 O: 그 외
	 * -> 모든 결과에 대해 exception 발생하지 않는다면, doFilter 진행
	 * 		-> exception 발생 시, doFilter 진행하지 않고, security exceptionHandler에서 처리
	 */
	protected void doFilterInternal(
		HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		String httpMethod = request.getMethod();
		String uri = request.getRequestURI();

		try {
			// 토큰 검증이 필요 없는지 확인 후 필요하면 토큰 검증으로 진행
			if (isTokenValidationRequired(request)) {

				// accessToken 추출(null 포함)
				String accessToken = getTokenFromCookies(CookieType.ACCESS_TOKEN);

				// 추출한 accessToken 유효성 검증 후 Authentication 반환 - 아니면 throw AccessDeniedException
				Authentication authentication = getAuthentication(accessToken);

				// SecurityContextHolder에 Authentication 저장
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}

		} catch (JwtException | AuthenticationException e) {
			if (!(httpMethod.equals("POST") && uri.equals("/api/logout"))) {
				throw new UsernameNotFoundException(e.getMessage());
			}
		}
		// 다음 필터로 이동
		filterChain.doFilter(request, response);
	}

	/**
	 * 추출한 accessToken 유효성 검증 후 Authentication 반환
	 *
	 * 1. accessToken 유효한 경우 -> 성공
	 * 2. accessToken 유효하지 않으면
	 * 	-> refreshToken 재발급 진행
	 * 	  -> refresh 유효하다면 accessToken 재발급 -> 성공
	 * 	  -> refresh 토큰 만료 -> 실패
	 *
	 * -> 성공 시 token에서 memberId, role 추출해 Authentication 생성 후 반환
	 * -> 실패 시 JwtException을 AccessDeniedException으로 변경해 throw
	 */
	private Authentication getAuthentication(String accessToken) {

		// accessToken이 유효하지 않다면,in
		if (!jwtTokenService.isTokenValidated(accessToken)) {

			// refresh 토큰 추출 (null 포함)
			String refreshToken = getTokenFromCookies(CookieType.REFRESH_TOKEN);

			// accessToken 재발급, refresh 토큰 만료 혹은 DB와 다르다면 throw
			accessToken = jwtTokenReissueService.reissueAccessToken(refreshToken);
		}

		// 유효한 accessToken으로 검증
		return jwtTokenService.makeAuthenticationFromToken(accessToken);

	}

	/**
	 *  cookies, cookie가 null이 아니고, token이 있다면 Token 반환, 없다면 null
	 * 	  -> accessToken이 null이면 refresh 토큰만 있는 경우
	 * 	  -> token 유효성 검증 시 null도 검증하기 때문에 null로 설정.
	 * @param cookieType accessToken, refreshToken 쿠키 이름
	 * @return 해당 token value or null
	 */
	private String getTokenFromCookies(CookieType cookieType) {

		// map 사용: Optional 객체가 비어있다면, 그대로 Optional 반환(null 반환으로 처리), 객체가 존재하면 함수 동작해 token 반환
		return
			cookieUtils.getCookieFromRequest(cookieType)
				.map(jwtTokenService::getTokenFromCookie)
				.orElseThrow(
					() -> new UsernameNotFoundException(ERROR_MESSAGE));

	}

	/**
	 * customAntPathMatcher를 이용해 토큰 검증이 필요한 httpMethod, uri인지 확인
	 * @return boolean 토큰 검증이 필요하면 true, 아니면 false
	 */
	private boolean isTokenValidationRequired(HttpServletRequest request) {
		return !customRequestMatcher.matches(request);
	}

}
