package goorm.eagle7.stelligence.common.auth.jwt;

import static jakarta.servlet.RequestDispatcher.*;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.member.model.Role;
import io.jsonwebtoken.Claims;
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
			.getClaimsOrNullIfInvalid(token).orElseThrow(
				() -> new UsernameNotFoundException(ERROR_MESSAGE)
			)
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
			.getClaimsOrNullIfInvalid(token)
				.orElseThrow(
					() -> new UsernameNotFoundException(ERROR_MESSAGE)
				)
			.get(jwtProperties.getClaims().getValue() , String.class));
	}

	/**
	 * <h2>만료된 토큰에서 subject(memberId) 추출</h2>
	 * @param token 만료된 토큰
	 * @return String subject(memberId)
	 */
	public String extractSubFromExpiredToken(String token) {

		try {
			return jwtTokenValidator
				.getClaimsOrNullIfInvalid(token)
				.map(Claims::getSubject).orElseThrow(
					() -> new UsernameNotFoundException(ERROR_MESSAGE)
				);
		} catch (ExpiredJwtException e) {
			log.debug("만료된 JWT에서 sbj 추출: {}", e.getMessage());
			return e.getClaims().getSubject();
		}

	}

}
