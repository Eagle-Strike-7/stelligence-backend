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
		boolean result = permittedPathStore.isPermittedAll(httpMethod, uri);

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
		boolean result = permittedPathStore.isPermittedAll(httpMethod, uri);

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
		boolean result = permittedPathStore.isPermittedAll(httpMethod, uri);

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
		boolean result = permittedPathStore.isPermittedAll(httpMethod, uri);

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
		boolean result = permittedPathStore.isPermittedAll(httpMethod, uri);

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
		boolean result = permittedPathStore.isPermittedAll(httpMethod, uri);

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
		boolean result = permittedPathStore.isPermittedAll(httpMethod, uri);

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
		boolean result = permittedPathStore.isPermittedAll(httpMethod, uri);

		// then
		assertThat(result).isTrue();

	}

	/** existMemberInfoRequired 메서드 테스트 */

	@Test
	@DisplayName("[성공] uri **에 많은 것이 있는 경우 - /api/contibutes/**/votes - existMemberInfoRequired")
	void existApiContributesVotesTrue() {

		// given
		String httpMethod = "GET";
		String uri = "/api/contributes/1/4/random/44//votes";

		// when
		boolean result = permittedPathStore.isRequiredMemberInfo(httpMethod, uri);

		// then
		assertThat(result).isTrue();

	}

	/** existMemberInfoRequired 메서드 테스트 */

	@Test
	@DisplayName("[성공] **에 아무것도 없는 경우 - /api/contributes/**/votes 경우 - existMemberInfoRequired")
	void existMemberInfoRequiredApiContributesVotesTrue() {

		// given
		String httpMethod = "GET";
		String uri = "/api/contributes/votes";

		// when
		boolean result = permittedPathStore.isRequiredMemberInfo(httpMethod, uri);

		// then
		assertThat(result).isTrue();

	}

	@Test
	@DisplayName("[예외] 메서드가 다른 경우, /api/contributes/**/votes 경우 - existMemberInfoRequired")
	void existMemberInfoRequiredApiContributesVotesFalse() {

		// given
		String httpMethod = "POST";
		String uri = "/api/contributes/votes";

		// when
		boolean result = permittedPathStore.isRequiredMemberInfo(httpMethod, uri);

		// then
		assertThat(result).isFalse();

	}

	@Test
	@DisplayName("[성공] /api/bookmarks/marked 경우")
	void existMemberInfoRequiredApiBookmarksMarkedTrue() {

		// given
		String httpMethod = "GET";
		String uri = "/api/bookmarks/marked";

		// when
		boolean result = permittedPathStore.isRequiredMemberInfo(httpMethod, uri);

		// then
		assertThat(result).isTrue();

	}

}