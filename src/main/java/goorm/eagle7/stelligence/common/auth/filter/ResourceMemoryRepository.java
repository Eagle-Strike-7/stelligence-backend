package goorm.eagle7.stelligence.common.auth.filter;

import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

/**
 * 인증이 필요한 리소스 정보를 arrayList로 저장해 놓은 객체이나 테스트 용도로 인증 필요없는 리소스만 저장해 놓음.
 * CustomAntPathMatcher에서 사용, 전체를 불러와 매칭할 때 사용함.
 */
@Repository
@NoArgsConstructor
public class ResourceMemoryRepository {
	private static final List<RequestResource> REQUEST_RESOURCES = List.of(
		RequestResource.of(HttpMethod.GET.name(), "/api/documents"),
		RequestResource.of(HttpMethod.GET.name(), "/api/contributes"),
		RequestResource.of(HttpMethod.GET.name(), "/api/comments"),
		RequestResource.of(HttpMethod.GET.name(), "/api/debates"),

		RequestResource.of(HttpMethod.GET.name(), "/oauth2/**"),
		RequestResource.of(HttpMethod.GET.name(), "/login/oauth2/code/**"),

		RequestResource.of(HttpMethod.GET.name(), "/swagger-ui/**"),
		RequestResource.of(HttpMethod.POST.name(), "/error/**"),

		RequestResource.of(HttpMethod.POST.name(), "/api/login")
	);

	public List<RequestResource> findAll() {
		return REQUEST_RESOURCES;
	}
}
