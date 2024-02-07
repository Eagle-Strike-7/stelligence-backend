package goorm.eagle7.stelligence.domain.member.model;

import java.util.Arrays;

import lombok.Getter;

@Getter
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

	/**
	 * <h2>사용자 권한 문자열로부터 Role 객체 반환</h2>
	 * @param roleStr 사용자 권한 문자열
	 * @return Role 객체
	 */
	public static Role fromValue(String roleStr) {
		return Arrays.stream(Role.values())
			.filter(role -> role.getValue().equalsIgnoreCase(roleStr))
			.findFirst()
			.orElse(Role.USER); // 일치하는 역할이 없으면 기본값으로 USER 반환
	}

}
