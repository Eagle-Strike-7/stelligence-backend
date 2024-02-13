package goorm.eagle7.stelligence.common.auth.filter.pathmatch;

import static org.assertj.core.api.Assertions.*;

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
class PermittedPathStoreTest {

	@InjectMocks
	private PermittedPathStore permittedPathStore;

	@Test
	@DisplayName("[정상] Base uri, 메서드가 같은 경우")
	void existBaseSameTrue() {

		// given
		String httpMethod = "GET";
		String uri = "/api/documents";

		// when
		boolean result = permittedPathStore.exist(httpMethod, uri);

		// then
		assertThat(result).isTrue();

	}

	@Test
	@DisplayName("[정상] 쿼리 파라미터 포함 uri, 메서드가 같은 경우")
	void existBaseQueryStringSameTrue() {

		// given
		String httpMethod = "GET";
		String uri = "/api/documents/search?query=1";

		// when
		boolean result = permittedPathStore.exist(httpMethod, uri);

		// then
		assertThat(result).isTrue();

	}

	@Test
	@DisplayName("[예외] uri는 같고, 메서드가 다른 경우")
	void existDifferMethodFalse() {

		// given
		String httpMethod = "POST";
		String uri = "/api/documents";

		// when
		boolean result = permittedPathStore.exist(httpMethod, uri);

		// then
		assertThat(result).isFalse();

	}

	@Test
	@DisplayName("[예외] uri가 다른 경우")
	void existDifferUriFalse() {

		// given
		String httpMethod = "GET";
		String uri = "/api/notin";

		// when
		boolean result = permittedPathStore.exist(httpMethod, uri);

		// then
		assertThat(result).isFalse();

	}

	@Test
	@DisplayName("[예외] uri 끝 한 글자 다른 경우")
	void existDifferUriOnlyOneFalse() {

		// given
		String httpMethod = "GET";
		String uri = "/api/document";

		// when
		boolean result = permittedPathStore.exist(httpMethod, uri);

		// then
		assertThat(result).isFalse();

	}

	@Test
	@DisplayName("[예외] api 제외 uri가 다른 경우")
	void existApiDifferUriFalse() {

		// given
		String httpMethod = "GET";
		String uri = "/notin";

		// when
		boolean result = permittedPathStore.exist(httpMethod, uri);

		// then
		assertThat(result).isFalse();

	}

	@Test
	@DisplayName("[예외] OPTINOS, /api/login인 경우")
	void existOptionsApiLoginTrue() {

		// given
		String httpMethod = "OPTIONS";
		String uri = "/api/login";

		// when
		boolean result = permittedPathStore.exist(httpMethod, uri);

		// then
		assertThat(result).isTrue();

	}

	@Test
	@DisplayName("[예외] OPTIONS, /api/members/me/nickname인 경우")
	void existOptionsApiMembersMeNicknameTrue() {

		// given
		String httpMethod = "OPTIONS";
		String uri = "/api/members/me/nickname";

		// when
		boolean result = permittedPathStore.exist(httpMethod, uri);

		// then
		assertThat(result).isTrue();

	}

}