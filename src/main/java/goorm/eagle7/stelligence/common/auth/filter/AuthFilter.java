package goorm.eagle7.stelligence.common.auth.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import goorm.eagle7.stelligence.api.ApiResponse;
import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.common.auth.jwt.JwtTokenService;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberContextHolder;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
import goorm.eagle7.stelligence.domain.member.model.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * test 시 @component 주석처리하기!!!
 * 토큰 검증이 필요한 리소스에 대해 토큰을 검증하고, ThreadLocal에 memberInfo를 저장한다.
 * 토큰 검증이 필요한 리소스는 CustomAntPathMatcher에서 정의한다.
 */
@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

	private final CustomAntPathMatcher customAntPathMatcher;
	private final JwtTokenService jwtTokenService;

	/**
	 * 1. request의 header에서 토큰 검증이 필요한 리소스인지 확인
	 * 2. request에서 token 추출
	 * 3. 추출한 token 유효성 검증
	 * 4. 검증 완료 이후 memberInfo를 ThreadLocal에 저장
	 * 5. BaseException 예외 발생 시 ApiResponse로 응답
	 * 6. 무슨 일이 있어도 ThreadLocal 초기화
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

		try {
			// 토큰 검증이 필요한 uri라면 토큰 검증
			if (isTokenValidationRequired(httpMethod, uri)) {

				// request에서 token 추출
				String token = jwtTokenService.extractJwtFromHeader(request);
				// 추출한 token 유효성 검증
				jwtTokenService.validateActiveToken(token);

				// 검증 완료 이후 memberInfo를 ThreadLocal에 저장

				// ThreadLocal 초기화
				MemberContextHolder.clear();
				// null이면 test 용으로 1L, USER 반환
				MemberInfo memberInfo = jwtTokenService.getMemberInfo(token);
				if (memberInfo == null) {
					memberInfo = MemberInfo.of(1L, Role.USER);
				}
				// ThreadLocal에 token에서 추출한 memberInfo 저장
				MemberContextHolder.setMemberInfo(memberInfo);
			}
			filterChain.doFilter(request, response);
		} catch (BaseException e) {
			// 사용자 정의 오류 응답 생성
			ApiResponse<Object> apiResponse = ApiResponse.fail(e.getMessage());

			// JSON으로 변환
			String jsonResponse = new ObjectMapper().writeValueAsString(apiResponse);

			// 응답 설정
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 상태 코드
			response.getWriter().write(jsonResponse);

		} finally {
			// 무슨 일이 있어도 ThreadLocal 초기화
			MemberContextHolder.clear();
		}
	}

	/**
	 * customAntPathMatcher를 이용해 토큰 검증이 필요한 httpMethod, uri인지 확인
	 * @param httpMethod String 타입으로 추출.
	 * @param uri uri String 타입으로 추출.
	 * @return boolean 토큰 검증이 필요하면 true, 아니면 false
	 */
	private boolean isTokenValidationRequired(String httpMethod, String uri) {
		return customAntPathMatcher.match(httpMethod, uri);
	}
}
