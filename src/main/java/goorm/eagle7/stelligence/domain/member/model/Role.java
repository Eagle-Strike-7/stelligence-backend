package goorm.eagle7.stelligence.domain.member.model;

import java.util.Arrays;

/**
 * <h2>사용자 권한</h2>
 * <p>ADMIN: 관리자</p>
 * <p>USER: 일반 사용자</p>
 */
public enum Role {
	ADMIN("ROLE_ADMIN"), USER("ROLE_USER");

	private final String value;

	Role(String value) {
		this.value = value;
	}

	public static Role fromValue(String value) {
		try {
			return Arrays.stream(values())
				.filter(r -> r.value.equalsIgnoreCase(value))
				.findAny()
				.orElseThrow(IllegalArgumentException::new);
		} catch (IllegalArgumentException e) {
			return Role.USER;
		}
	}

}
