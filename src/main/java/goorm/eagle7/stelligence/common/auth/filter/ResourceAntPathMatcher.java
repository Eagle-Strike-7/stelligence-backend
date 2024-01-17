package goorm.eagle7.stelligence.common.auth.filter;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

/**
 * Request의 method, path가 검증 대상인지 확인 후, 검증 대상인지를 알려준다.
 */
@Component
public class ResourceAntPathMatcher extends AntPathMatcher {

	private final List<Resource> resources;

	public ResourceAntPathMatcher(ResourceMemoryRepository resourceMemoryRepository){
		this.resources = resourceMemoryRepository.findAll();
	}

	/**
	 * 요청의 (method, path) 쌍이 리소스 리스트 중 어느 하나라도 일치하는 게 있는지 확인
	 * @param httpMethod httpMethod 타입(GET, POST, PUT, DELETE)
	 * @param uri uri
	 * @return boolean 리소스 리스트에 있으면 true, 없으면 false
	 */
	@Override
	public boolean match(String httpMethod, String uri) {
		return resources.stream()
			.anyMatch(r -> r.getHttpMethod().equals(httpMethod) && super.match(r.getUri(), uri));
	}

}
