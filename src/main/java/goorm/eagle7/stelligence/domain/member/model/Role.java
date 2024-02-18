package goorm.eagle7.stelligence.domain.member.model;

import java.util.Arrays;

import lombok.Getter;

/**
 * <h2>사용자 권한</h2>
 * <p>ADMIN: 관리자</p>
 * <p>USER: 일반 사용자</p>
 */
@Getter
public enum Role {
	ADMIN("ROLE_ADMIN"), USER("ROLE_USER");

	private final String value;

	Role(String value) {
		this.value = value;
	}

	public static Role fromValueDefaultUser(String value) {
		try {
			return fromValue(value);
		} catch (IllegalArgumentException e) {
			return Role.USER;
		}
	}

	private static Role fromValue(String value) {
		return Arrays.stream(values())
			.filter(r -> r.value.equalsIgnoreCase(value))
			.findAny()
			.orElseThrow(IllegalArgumentException::new);
	}

}
