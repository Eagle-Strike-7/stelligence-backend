package goorm.eagle7.stelligence.common.auth.filter.pathmatch;

import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * <h2>RequestMatcher</h2>
 * <p>Request의 {httpMethod, uri}가 검증 대상인지 확인</p>
 */
@Component
@RequiredArgsConstructor
public class CustomRequestMatcher implements RequestMatcher {

	private final PermittedPathStore permittedPathStore;

	/**
	 * <h2>permitAll match 확인</h2>
	 * <p>요청의 httpMethod, uri 리소스 리스트 중 어느 하나라도 일치하는 게 있는지 확인</p>
	 * @param request 요청
	 * @return boolean 리소스 리스트에 있으면 true, 없으면 false
	 */
	@Override
	public boolean matches(HttpServletRequest request) {

		String httpMethod = request.getMethod();
		String uri = request.getRequestURI();

		return permittedPathStore.isPermittedAll(httpMethod, uri);

	}

	/**
	 * <h2>permitAll 중 토큰 검증이 필요한지 확인</h2>
	 * @param httpMethod
	 * @param uri
	 * @return
	 */
	public boolean matchesMemberInfoRequiredInPermitAll(String httpMethod, String uri) {
		return permittedPathStore.isRequiredMemberInfo(httpMethod, uri);
	}

}
