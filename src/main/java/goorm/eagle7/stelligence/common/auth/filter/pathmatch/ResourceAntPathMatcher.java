package goorm.eagle7.stelligence.common.auth.filter.pathmatch;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import lombok.RequiredArgsConstructor;

/**
 * Request의 method, path가 검증 대상인지 확인 후, 검증 대상인지를 알려준다.
 */
@Component
@RequiredArgsConstructor
public class ResourceAntPathMatcher extends AntPathMatcher {

	private final PermittedPathStore permittedPathStore;

	/**
	 * 요청의 (method, path) 쌍이 리소스 리스트 중 어느 하나라도 일치하는 게 있는지 확인
	 * @param httpMethod httpMethod 타입(GET, POST, PUT, DELETE)
	 * @param uri uri
	 * @return boolean 리소스 리스트에 있으면 true, 없으면 false
	 */
	@Override
	public boolean match(String httpMethod, String uri) {

		return permittedPathStore.exist(httpMethod, uri);

	}

}
