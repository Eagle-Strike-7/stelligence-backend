package goorm.eagle7.stelligence.common.auth.filter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;


/**
 * 인증이 필요없는 리소스 정보를 담는 객체
 * SecurityConfig, AuthFilter에서 사용
 */
@Repository
public class ResourceMemoryRepository {
	private static final List<RequestResource> REQUEST_RESOURCES = new ArrayList<>();

	public ResourceMemoryRepository() {

		REQUEST_RESOURCES.add(RequestResource.of(HttpMethod.GET.name(), "/api/documents"));
		REQUEST_RESOURCES.add(RequestResource.of(HttpMethod.GET.name(), "/api/contributes"));
		REQUEST_RESOURCES.add(RequestResource.of(HttpMethod.GET.name(), "/api/comments"));
		REQUEST_RESOURCES.add(RequestResource.of(HttpMethod.GET.name(), "/api/debates"));

		REQUEST_RESOURCES.add(RequestResource.of(HttpMethod.GET.name(), "/oauth2/**"));
		REQUEST_RESOURCES.add(RequestResource.of(HttpMethod.GET.name(), "/login/oauth2/code/**"));

		REQUEST_RESOURCES.add(RequestResource.of(HttpMethod.GET.name(), "/swagger-ui/**"));

		// 404 Not Found
		REQUEST_RESOURCES.add(RequestResource.of(HttpMethod.POST.name(), "/error/**"));

		// REQUEST_RESOURCES.add(RequestResource.of("POST", "/oauth2/**"));
		REQUEST_RESOURCES.add(RequestResource.of(HttpMethod.POST.name(), "/api/login"));

	}

	public List<RequestResource> findAll() {
		return REQUEST_RESOURCES;
	}
}
