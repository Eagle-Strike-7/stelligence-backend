package goorm.eagle7.stelligence.common.auth.filter;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * <h2>허용 리소스 정보 메모리 저장소 테스트</h2>
 * <p>method, BaseUri 인식하는지 위주로 테스트 진행</p>
 */
@ExtendWith(MockitoExtension.class)
class PermitPathStoreTest {

	@InjectMocks
	private PermitPathStore permitPathStore;

	@Test
	@DisplayName("[정상] Base uri, 메서드가 같은 경우")
	void existBaseSameTrue() {

		// given
		RequestResource resource = RequestResource.of("GET", "/api/documents");

		// when
		boolean result = permitPathStore.exist(resource);

		// then
		assertThat(result).isTrue();

	}

	@Test
	@DisplayName("[정상] 쿼리 파라미터 포함 uri, 메서드가 같은 경우")
	void existBaseQueryStringSameTrue() {

		// given
		RequestResource resource = RequestResource.of("GET", "/api/documents/search?query=1");

		// when
		boolean result = permitPathStore.exist(resource);

		// then
		assertThat(result).isTrue();

	}

	@Test
	@DisplayName("[예외] uri는 같고, 메서드가 다른 경우")
	void existDifferMethodFalse() {

		// given
		RequestResource methodDiffer = RequestResource.of("POST", "/api/documents");

		// when
		boolean result = permitPathStore.exist(methodDiffer);

		// then
		assertThat(result).isFalse();

	}

	@Test
	@DisplayName("[예외] uri가 다른 경우")
	void existDifferUriFalse() {

		// given
		RequestResource uriDiffer = RequestResource.of("GET", "/api/notin");

		// when
		boolean result = permitPathStore.exist(uriDiffer);

		// then
		assertThat(result).isFalse();

	}

	@Test
	@DisplayName("[예외] uri 끝 한 글자 다른 경우")
	void existDifferUriOnlyOneFalse() {

		// given
		RequestResource uriDiffer = RequestResource.of("GET", "/api/document");

		// when
		boolean result = permitPathStore.exist(uriDiffer);

		// then
		assertThat(result).isFalse();

	}

	@Test
	@DisplayName("[예외] api 제외 uri가 다른 경우")
	void existApiDifferUriFalse() {

		// given
		RequestResource uriDiffer = RequestResource.of("GET", "/notin");

		// when
		boolean result = permitPathStore.exist(uriDiffer);

		// then
		assertThat(result).isFalse();

	}

}