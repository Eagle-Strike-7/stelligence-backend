package goorm.eagle7.stelligence.common.auth.filter;

import java.util.Objects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Filter에서 사용, 인증이 필요한 리소스 정보를 담는 객체
 * CustomAntPathMatcher에서 사용
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "of") // 정적 팩토리 메서드
public class RequestResource {
	private String httpMethod;
	private String uri;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		RequestResource that = (RequestResource)o;
		return Objects.equals(httpMethod, that.httpMethod) && Objects.equals(uri, that.uri);
	}

	@Override
	public int hashCode() {
		return Objects.hash(httpMethod, uri);
	}
}
