package goorm.eagle7.stelligence.domain.member.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
	ADMIN("ROLE_ADMIN"), USER("ROLE_USER");

	private String value;

	public static Role getRoleFromString(String roleStr) {
		try {
			return Role.valueOf(roleStr.toUpperCase());
		} catch (IllegalArgumentException e) {
			return Role.USER;
		}
	}
}
