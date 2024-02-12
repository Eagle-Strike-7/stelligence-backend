package goorm.eagle7.stelligence.common.auth.filter;

import static java.nio.charset.StandardCharsets.*;
import static org.springframework.http.MediaType.*;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.common.auth.jwt.JwtTokenReissueService;
import goorm.eagle7.stelligence.common.auth.jwt.JwtTokenService;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfoContextHolder;
import goorm.eagle7.stelligence.common.login.CookieType;
import goorm.eagle7.stelligence.common.login.CookieUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

	private final ResourceAntPathMatcher resourceAntPathMatcher;
	private final JwtTokenService jwtTokenService;
	private final JwtTokenReissueService jwtTokenReissueService;
	private final CookieUtils cookieUtils;

	/**
	 * 1. request의 header에서 토큰 검증이 필요한 리소스인지 확인
	 * 2. request에서 token 추출
	 * 3. 추출한 token 유효성 검증
	 * 4. 검증 완료 이후 memberInfo를 ThreadLocal에 저장
	 * 5. BaseException 예외 발생 시 ApiResponse로 응답
	 * 	- 401 상태 코드(인증 실패, UNAUTHORIZED)로 응답
	 * 6. 무슨 일이 있어도 ThreadLocal 초기화
	 */
	@Override
	protected void doFilterInternal(
		HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
		ServletException,
		IOException {

		String httpMethod = request.getMethod();
		String uri = request.getRequestURI();

		try {
			// 토큰 검증이 필요한 uri라면 토큰 검증
			if (!isTokenValidationRequired(httpMethod, uri)) {

				Cookie cookie = cookieUtils.getCookieFromRequest(CookieType.ACCESS_TOKEN).orElseThrow(
					() -> new BaseException("로그인이 필요합니다.")
				);

				String accessToken = null;

				// request에서 accessToken 추출
				accessToken = jwtTokenService.extractJwtFromCookie(cookie, CookieType.ACCESS_TOKEN);

				// accessToken이 유효하지 않다면
				if (!jwtTokenService.validateToken(accessToken)) {

					String refreshToken = jwtTokenService.extractJwtFromCookie(cookie, CookieType.REFRESH_TOKEN);

					// accessToken 재발급, refresh 토큰 만료라면 throw BaseException
					accessToken = jwtTokenReissueService.reissueAccessToken(refreshToken);

				}

				Claims claims = jwtTokenService.validateAndGetClaims(accessToken);

				/**
				 * 검증 완료 이후 memberInfo를 ThreadLocal에 저장
				 */

				// ThreadLocal 초기화
				MemberInfoContextHolder.clear();
				MemberInfo memberInfo = jwtTokenService.getMemberInfo(claims);

				// ThreadLocal에 token에서 추출한 memberInfo 저장
				MemberInfoContextHolder.setMemberInfo(memberInfo);
			}

			filterChain.doFilter(request, response);

		} catch (BaseException e) {

			// 로그아웃 시에는 토큰 검증이 필요 없음, 로그아웃 요청이 아니면 다시 같은 ex 발생
			if ((httpMethod.equals("POST") && uri.equals("/api/logout")
				|| (httpMethod.equals("OPTIONS") && uri.equals(
				"/api/logout")))) {
				filterChain.doFilter(request, response);
			} else {

				// BaseException 예외 발생 시 ApiResponse로 응답

				log.debug("BaseException catched in AuthFilter : {}", e.getMessage());
				// 사용자 정의 오류 응답 생성
				ResponseTemplate<Void> apiResponse = ResponseTemplate.fail(e.getMessage());

				// JSON으로 변환
				String jsonResponse = new ObjectMapper().writeValueAsString(apiResponse);

				// 응답 설정, application/json, UTF-8 enum으로 설정
				response.setContentType(APPLICATION_JSON_VALUE);
				response.setCharacterEncoding(UTF_8.name());
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드
				response.getWriter().write(jsonResponse);

			}

		} finally {
			// 무슨 일이 있어도 ThreadLocal 초기화
			MemberInfoContextHolder.clear();
		}

	}

	/**
	 * customAntPathMatcher를 이용해 토큰 검증이 필요한 httpMethod, uri인지 확인
	 * @param httpMethod String 타입으로 추출.
	 * @param uri uri String 타입으로 추출.
	 * @return boolean 토큰 검증이 필요하면 true, 아니면 false
	 */
	private boolean isTokenValidationRequired(String httpMethod, String uri) {
		return resourceAntPathMatcher.match(httpMethod, uri);
	}
}
