package goorm.eagle7.stelligence.common.auth.filter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;


/**
 * 인증이 필요한 리소스 정보를 arrayList로 저장해 놓은 객체
 * CustomAntPathMatcher에서 사용, 전체를 불러와 매칭할 때 사용함.
 */
@Repository
public class MemoryResourceRepository {
	private static final List<Resource> resources = new ArrayList<>();

	public MemoryResourceRepository() {

		resources.add(Resource.of("GET", "/api/"));
		resources.add(Resource.of("GET", "/api/members/me"));
	}

	public List<Resource> findAll() {
		return resources;
	}
}
