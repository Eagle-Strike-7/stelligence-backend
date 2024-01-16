package goorm.eagle7.stelligence.common.auth.jwt;

/**
 * JWT 토큰 관련 유틸리티 - public X
 * 역할: JWT와 관련된 유틸리티 기능 제공
 * 	- 유틸리티: 일반적으로 재사용 가능한 메서드나 기능들의 집합, 상태를 저장하지 않으며, 주로 정적 메서드로 구성됨. bean으로 등록 X
 * 책임:
 *  - Bearer 접두어 추가 및 제거
 *  - 토큰의 만료 시간 계산
 *  - 기타 반복적으로 사용되는 JWT 관련 작업
 */
class JwtTokenUtil {

	private static final String BEARER = "Bearer ";

	/**
	 * 인스턴스로 생성되는 것을 막기 위해 private 생성자
	 */
	private JwtTokenUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Authorization 필드값에서 Bearer 접두어 제거
	 * @param token Authorization 필드값(Bearer 접두어 포함)
	 * @return String token(Bearer 접두어 제거)
	 */
	public static String removeBearerPrefix(String token) {
		return token.replace(BEARER, "");
	}

}
