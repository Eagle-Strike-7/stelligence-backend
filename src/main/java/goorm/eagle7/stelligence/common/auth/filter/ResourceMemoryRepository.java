package goorm.eagle7.stelligence.common.auth.filter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;


/**
 * 인증이 필요한 리소스 정보를 arrayList로 저장해 놓은 객체이나 테스트 용도로 인증 필요없는 리소스만 저장해 놓음.
 * CustomAntPathMatcher에서 사용, 전체를 불러와 매칭할 때 사용함.
 */
@Repository
public class ResourceMemoryRepository {
	private static final List<Resource> resources = new ArrayList<>();

	public ResourceMemoryRepository() {

		resources.add(Resource.of("POST", "/api/login"));

	}

	public List<Resource> findAll() {
		return resources;
	}
}
