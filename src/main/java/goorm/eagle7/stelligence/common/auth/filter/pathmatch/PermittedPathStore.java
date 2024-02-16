package goorm.eagle7.stelligence.common.auth.filter.pathmatch;

import java.util.Set;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;
import org.springframework.util.AntPathMatcher;

import lombok.NoArgsConstructor;

/**
 * <h2>허용 리소스 정보 메모리 저장소</h2>
 * <p>- 로그인 필요 없는 리소스(httpMethod, uri) 정보를 Set으로 저장</p>
 */
@Repository
@NoArgsConstructor
class PermittedPathStore {

	private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

	/**
	 * <h2>모든 허용 리소스 정보</h2>
	 * <p>- 로그인 필요 없는 리소스(httpMethod, uri) 정보를 Set으로 저장</p>
	 */
	private static final Set<RequestResource> PERMITTED_RESOURCES =
		Set.of(

			// application 권한
			RequestResource.of(HttpMethod.GET.name(), "/api/documents/**"),
			RequestResource.of(HttpMethod.GET.name(), "/api/contributes/**"),
			RequestResource.of(HttpMethod.GET.name(), "/api/debates/**"),
			RequestResource.of(HttpMethod.GET.name(), "/api/bookmarks/marked/**"),

			// oauth2
			RequestResource.of(HttpMethod.GET.name(), "/oauth2/authorization/**"),
			RequestResource.of(HttpMethod.GET.name(), "/login/oauth2/code/**"),

			// swagger
			RequestResource.of(HttpMethod.GET.name(), "/api-docs/**"),
			RequestResource.of(HttpMethod.GET.name(), "/swagger-ui/**"),
			RequestResource.of(HttpMethod.GET.name(), "/swagger-resources/**"),
			RequestResource.of(HttpMethod.GET.name(), "/v3/api-docs/**"),

			// error
			RequestResource.of(HttpMethod.POST.name(), "/error/**"),

			// 정적 리소스
			RequestResource.of(HttpMethod.GET.name(), "/css/**"),
			RequestResource.of(HttpMethod.GET.name(), "/images/**"),
			RequestResource.of(HttpMethod.GET.name(), "/favicon.ico"),
			RequestResource.of(HttpMethod.GET.name(), "/fonts/**"),
			RequestResource.of(HttpMethod.GET.name(), "/assets/**"),
			RequestResource.of(HttpMethod.GET.name(), "/badges/**"),

			// 모니터링 툴
			RequestResource.of(HttpMethod.GET.name(), "/actuator/**"),

			// login - dev
			RequestResource.of(HttpMethod.POST.name(), "/api/login")

		);


	/**
	 * <h2>허용하는 uri 확인</h2>
	 * <p>- 요청의 httpMethod, uri가 리소스 리스트 중 어느 하나라도 일치하는 게 있는지 확인</p>
	 * <p>- 허용하는 uri가 /**로 끝나면, basePath 비교</p>
	 * @param httpMethod httpMethod 타입
	 * @param uri uri
	 * @return boolean 리소스 리스트에 있으면 true, 없으면 false
	 */
	public boolean isPermittedAll(String httpMethod, String uri) {

		for (RequestResource resource : PERMITTED_RESOURCES) {

			String permittedHttpMethod = resource.getHttpMethod();
			String permittedUri = resource.getUri();

			if (permittedHttpMethod.equals(httpMethod) &&
				antPathMatcher.match(permittedUri, uri)) {
				return true;
			}

		}

		return false;

	}

}