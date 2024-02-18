package goorm.eagle7.stelligence.domain.member.model;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;

/**
 * <h2>사용자 권한</h2>
 * <p>ADMIN: 관리자</p>
 * <p>USER: 일반 사용자</p>
 */
@Getter
public enum Role {
	ADMIN("ROLE_ADMIN"), USER("ROLE_USER");

	private final String label;
	private static final Map<String, Role> labels =
		Collections
			.unmodifiableMap(
				Stream.of(values()).collect(
					Collectors.toMap(
						Role::getLabel,
						Function.identity() // identity()는 입력값 그대로 반환
					))
			);

	Role(String label) {
		this.label = label;
	}

	/**
	 * <h2>label로 role 생성, 없는 label이면 default 설정</h2>
	 * @param label label
	 * @return Role default User
	 */
	public static Role fromLabelDefaultUser(String label) {
		try {
			return fromLabel(label);
		} catch (IllegalArgumentException e) {
			return Role.USER;
		}
	}

	private static Role fromLabel(String label) {
		return Optional
			.ofNullable(labels.get(label))
			.orElse(USER);
	}

}
