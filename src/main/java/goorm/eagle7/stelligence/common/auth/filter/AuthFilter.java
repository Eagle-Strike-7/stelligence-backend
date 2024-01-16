package goorm.eagle7.stelligence.common.auth.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import goorm.eagle7.stelligence.common.auth.jwt.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * test 시 @component 주석처리하기!!!
 * 토큰 검증이 필요한 리소스에 대해 토큰을 검증한다.
 * 토큰 검증이 필요한 리소스는 CustomAntPathMatcher에서 정의한다.
 */
// @Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

	private final CustomAntPathMatcher customAntPathMatcher;
	private final JwtTokenService jwtTokenService;
	private static final String MEMBER_INFO = "memberInfo";

	/**
	 * request의 header에서 토큰 검증이 필요한 리소스에 대해 토큰을 검증하고, 검증이 완료됐다면 해당 토큰을 다시 request에 담아준다..
	 * request에
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param filterChain FilterChain
	 * @throws ServletException
	 * @throws IOException
	 */
	@Override
	protected void doFilterInternal(
		HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String httpMethod = request.getMethod();
		String uri = request.getRequestURI();

		// TODO 검증 예외 고려 필요
		// 토큰 검증이 필요한 uri라면 토큰 검증
		if (isTokenValidationRequired(httpMethod, uri)) {

			String token = JwtTokenService.extractJwtFromHeader(request);
			jwtTokenService.validateActiveToken(token);

			// 검증 완료 후 request에 memberInfo 저장
			request.setAttribute(MEMBER_INFO, jwtTokenService.getMemberInfo(token));
		}
		filterChain.doFilter(request, response);
	}

	/**
	 * customAntPathMatcher를 이용해 토큰 검증이 필요한 httpMethod, uri인지 확인
	 * @param httpMethod
	 * @param uri
	 * @return boolean 토큰 검증이 필요하면 true, 아니면 false
	 */
	private boolean isTokenValidationRequired(String httpMethod, String uri) {
		return customAntPathMatcher.match(httpMethod, uri);
	}
}
