package goorm.eagle7.stelligence.domain.member.model;

/**
 * <h2>사용자 권한</h2>
 * <p>ADMIN: 관리자</p>
 * <p>USER: 일반 사용자</p>
 */
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
