package goorm.eagle7.stelligence.common.auth.filter;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Request의 method, path가 검증 대상인지 확인 후, 검증 대상인지를 알려준다.
 */
@Component
public class RequestMatcher implements org.springframework.security.web.util.matcher.RequestMatcher {

	private final List<RequestResource> requestResources;
	private final AntPathMatcher pathMatcher;

	public RequestMatcher(ResourceMemoryRepository resourceMemoryRepository){
		this.requestResources = resourceMemoryRepository.findAll();
		this.pathMatcher= new AntPathMatcher();
	}

	/**
	 * 요청의 httpMethod, uri 리소스 리스트 중 어느 하나라도 일치하는 게 있는지 확인
	 * @param request 요청
	 * @return boolean 리소스 리스트에 있으면 true, 없으면 false
	 */
	@Override
	public boolean matches(HttpServletRequest request) {
		String httpMethod = request.getMethod();
		String uri = request.getRequestURI();

		return requestResources.stream()
			.anyMatch(r -> r.getHttpMethod().equals(httpMethod) && pathMatcher.match(r.getUri(), uri));
	}

	// TODO : 추후에 필요하면 추가 구현
	@Override
	public MatchResult matcher(HttpServletRequest request) {
		return org.springframework.security.web.util.matcher.RequestMatcher.super.matcher(request);
	}
}
