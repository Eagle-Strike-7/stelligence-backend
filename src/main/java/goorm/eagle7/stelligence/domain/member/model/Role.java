package goorm.eagle7.stelligence.domain.member.model;

public enum Role {
	ADMIN, USER;

	public static Role getRoleFromString(String roleStr) {
		try {
			return Role.valueOf(roleStr.toUpperCase());
		} catch (IllegalArgumentException e) {
			return Role.USER;
		}
	}
}
