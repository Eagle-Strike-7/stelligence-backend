package goorm.eagle7.stelligence.common.auth.filter;

import java.io.IOException;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import goorm.eagle7.stelligence.common.auth.filter.pathmatch.CustomRequestMatcher;
import goorm.eagle7.stelligence.common.auth.jwt.JwtTokenReissueService;
import goorm.eagle7.stelligence.common.auth.jwt.JwtTokenService;
import goorm.eagle7.stelligence.common.login.dto.LoginTokenInfo;
import goorm.eagle7.stelligence.common.util.CookieType;
import goorm.eagle7.stelligence.common.util.CookieUtils;
import goorm.eagle7.stelligence.common.util.ResponseUtils;
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
	@Override
	protected void doFilterInternal(
		HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		log.debug("AuthFilter 실행");
		String httpMethod = request.getMethod();
		String uri = request.getRequestURI();

		try {
			// 토큰 검증이 필요 없는지 확인 후 필요하면 토큰 검증으로 진행
			if (isTokenValidationRequired(request)) {

				log.debug("토큰 검증 필요");

				// accessToken 유효성 검사
				// 필요하다면 refresh 토큰으로 재발급
				// 만료된 토큰이면 재로그인 필요
				String activeAccessToken = getActiveAccessToken();

				// Authentication 반환
				Authentication authentication = jwtTokenService.makeAuthenticationFrom(activeAccessToken);

				// SecurityContextHolder에 Authentication 저장
				SecurityContextHolder.getContext().setAuthentication(authentication);

			}

		} catch (AuthenticationException e) {

			// Login 필수인 경우, 재로그인 필요
			if (!isMemberInfoRequired(httpMethod, uri)) {
				if (uri.equals("/api/members/me") && httpMethod.equals("GET")) {
					log.debug("[403] /api/members/me 403 error : {}", e.getMessage());
					ResponseUtils.sendErrorResponse(HttpServletResponse.SC_FORBIDDEN, ERROR_MESSAGE);
					return;
				}
				throw new UsernameNotFoundException(e.getMessage());
			}

			log.debug("[ex] 로그인 필수가 아닌 uri에서 로그인 사용자와 아닌 사용자를 구분. : {}", e.getMessage());
			saveAuthenticationForNullMemberInfo();

		}

		// 다음 필터로 이동
		filterChain.doFilter(request, response);

	}

	/**
	 * <h2>SecurityContextHolder에 anonymousUser Authentication 저장</h2>
	 * <p>- MemberInfo Resolver에서 anonymousUser 확인해 사용.</p>
	 * TODO : anonymous token으로 사용 예정.
	 */
	private static void saveAuthenticationForNullMemberInfo() {
		UserDetails userForNullMemberInfo =
			User.withUsername("anonymousUser").password("")
				.authorities("ROLE_USER").build();
		AnonymousAuthenticationToken authentication = new AnonymousAuthenticationToken(
			"anonymousUser", userForNullMemberInfo, userForNullMemberInfo.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	/**
	 * <h2>쿠키에서 token 추출</h2>
	 * <p>- cookieType에 따라 accessToken, refreshToken 쿠키에서 token 추출</p>
	 * <p>- accesscookie가 null인 경우, refresh 재발급 진행</p>
	 * <p>- refreshcookie가 null인 경우, 재로그인 필요</p>
	 * <p>- token이 없으면 재로그인 필요</p>
	 *  cookies, cookie가 null이 아니고, token이 있다면 Token 반환, 없다면 null
	 * 	  -> accessToken이 null이면 refresh 토큰만 있는 경우
	 * 	  -> token 유효성 검증 시 null도 검증하기 때문에 null로 설정.
	 * @return 해당 token value or null
	 */
	private String getActiveAccessToken() {

		return cookieUtils
			.getCookieFromRequest(CookieType.ACCESS_TOKEN)
			.map(
				cookie -> {
					log.debug("accessCookie가 있습니다. 유효성 검사 진행");
					// token 없으면 재로그인, 있으면 token 반환, 만료면 재발급
					String accessToken = cookie.getValue();
					if (!StringUtils.hasText(accessToken)) {
						return getAcccessTokenFromRefreshCookie();
					}
					return accessToken;
				})
			.orElseGet(() -> {
					// refresh cookie 확인, 없으면 재로그인, 있으면 재발급 진행
					log.debug("accessCookie가 없습니다. refresh 토큰으로 재발급 시도");
					return getAcccessTokenFromRefreshCookie();
				}
			);

	}

	// refresh 쿠키는 없으면 재로그인
	private String getAcccessTokenFromRefreshCookie() {
		String refreshToken = cookieUtils
			.getCookieFromRequest(CookieType.REFRESH_TOKEN)
			.orElseThrow(() -> new UsernameNotFoundException("Access 쿠키가 없습니다. Refresh 쿠키도 없습니다. 재로그인이 필요합니다."))
			.getValue();

		// refresh 토큰 없으면 재로그인, 있으면 재발급
		if (!StringUtils.hasText(refreshToken)) {
			throw new UsernameNotFoundException("Refresh 토큰이 없습니다. 재로그인이 필요합니다.");
		}

		LoginTokenInfo loginTokenInfo = jwtTokenReissueService
			.reissueAccessToken(refreshToken);

		String accessToken = loginTokenInfo.getAccessToken();
		cookieUtils.addCookieBy(CookieType.ACCESS_TOKEN, accessToken);
		String newRefreshToken = loginTokenInfo.getRefreshToken();
		cookieUtils.addCookieBy(CookieType.REFRESH_TOKEN, newRefreshToken);

		return accessToken;

	}

	/**
	 * customAntPathMatcher를 이용해 토큰 검증이 필요한 httpMethod, uri인지 확인
	 * @return boolean 토큰 검증이 필요하면 true, 아니면 false
	 */
	private boolean isTokenValidationRequired(HttpServletRequest request) {
		return !customRequestMatcher.matches(request);
	}

	private boolean isMemberInfoRequired(String httpMethod, String uri) {
		return customRequestMatcher.matchesMemberInfoRequiredInPermitAll(httpMethod, uri);
	}

}
