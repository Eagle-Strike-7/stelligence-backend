package goorm.eagle7.stelligence.common.auth.jwt;

import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.member.model.Role;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// TODO extends JwtDefaultParser
@Slf4j
@Component
@RequiredArgsConstructor
class JwtTokenParser {

	private final JwtProperties jwtProperties;
	private final JwtTokenValidator jwtTokenValidator;

	/**
	 * <h2>token에서 sub(memberId) 추출</h2>
	 * @param token token
	 * @return memberId 현재 로그인한 사용자의 memberId
	 */
	public String getSubject(String token) {
		log.debug("token에서 sub(memberId) 추출");
		return jwtTokenValidator
			.validateAndExtractClaims(token)
			.getSubject();
	}

	/**
	 * <h2>토큰에서 claims(role) 추출</h2>
	 * @param token 토큰
	 * @return Role claims(role)
	 */
	public Role getRole(String token) {
		log.debug("토큰에서 claims(role) 추출");
		return Role.fromValue(jwtTokenValidator
			.validateAndExtractClaims(token)
			.get(jwtProperties.getClaims().getValue(), String.class));
	}

	/**
	 * <h2>만료된 토큰에서 subject(memberId) 추출</h2>
	 * @param token 만료된 토큰
	 * @return String subject(memberId)
	 */
	public String extractSubFromExpiredToken(String token) {

		try {
			return jwtTokenValidator
				.validateAndExtractClaims(token)
				.getSubject();
		} catch (ExpiredJwtException e) {
			log.debug("만료된 JWT에서 sbj 추출: {}", e.getMessage());
			return e.getClaims().getSubject();
		}

	}

}
