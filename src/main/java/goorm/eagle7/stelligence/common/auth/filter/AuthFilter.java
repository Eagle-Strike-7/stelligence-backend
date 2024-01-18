package goorm.eagle7.stelligence.common.auth.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.common.auth.jwt.JwtTokenReissueService;
import goorm.eagle7.stelligence.common.auth.jwt.JwtTokenService;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfoContextHolder;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

// @Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

	@Value("${jwt.accessToken.name}")
	private String accessTokenName;
	@Value("${jwt.refreshToken.name}")
	private String refreshTokenName;

	private final ResourceAntPathMatcher resourceAntPathMatcher;
	private final JwtTokenService jwtTokenService;
	private final JwtTokenReissueService jwtTokenReissueService;

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

				Cookie[] cookies = request.getCookies();

				String accessToken = null;
				if (cookies != null) {
					// request에서 accessToken 추출
					accessToken = jwtTokenService.extractJwtFromCookie(request, accessTokenName);
				}

				// accessToken이 유효하지 않다면
				if (!jwtTokenService.validateToken(accessToken)) {

					String refreshToken = jwtTokenService.extractJwtFromCookie(request, refreshTokenName);

					// accessToken 재발급, refresh 토큰 만료라면 throw BaseException
					 accessToken = jwtTokenReissueService.reissueAccessToken(response, refreshToken, accessTokenName, refreshTokenName);

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

			// 사용자 정의 오류 응답 생성
			ResponseTemplate<Void> apiResponse = ResponseTemplate.fail(e.getMessage());

			// JSON으로 변환
			String jsonResponse = new ObjectMapper().writeValueAsString(apiResponse);

			// 응답 설정
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드
			response.getWriter().write(jsonResponse);

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
