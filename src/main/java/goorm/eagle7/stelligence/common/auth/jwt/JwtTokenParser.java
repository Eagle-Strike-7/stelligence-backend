package goorm.eagle7.stelligence.common.auth.jwt;

import java.util.Optional;

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

	private final JwtTokenValidator jwtTokenValidator;

	private static final String ERROR_MESSAGE = "유효하지 않은 사용자입니다.";

	// getClaims
	public Optional<Claims> getClaims(String token) {

		return jwtTokenValidator
			.getClaimsOrNullIfInvalid(token);

	}


	/**
	 * <h2>token에서 sub(memberId) 추출</h2>
	 * <p>- subject 얻는 건 refresh에서만 진행하기 때문에 token이 만료인 경우(empty),  재로그인 필요</p>
	 * @param claims token
	 * @return memberId 현재 로그인한 사용자의 memberId
	 * @throws UsernameNotFoundException 유효하지 않은 사용자입니다.
	 */
	public String getSubject(Claims claims) {

		log.debug("token에서 sub(memberId) 추출");

		// serviceTime 기준 만료 시각 이전인지 확인

		return claims
			.getSubject();

	}

	/**
	 * <h2>토큰에서 claims(role) 추출</h2>
	 * <p>- token이 만료인 경우(empty), RetryException 발생</p>
	 * @param claims 토큰
	 * @return Role claims(role)
	 */
	public Role getRole(Claims claims, String claimKey) {

		log.debug("토큰에서 사용자 정의 claims 추출");
		return Role.fromValue(
			claims.get(claimKey, String.class)
		);

	}



	/* dev */


	/**
	 * <h2>만료된 토큰에서 subject(memberId) 추출</h2>
	 * <p>- token이 만료인 경우(empty), RetryException 발생</p>
	 * <p>- dev로만 사용, 기존이라면 운영이라면 않아야 함.</p>
	 * @param token 만료된 토큰
	 * @return String subject(memberId)
	 */
	public String extractSubFromExpiredToken(String token) {

		try {
			return jwtTokenValidator
				.getClaimsOrNullIfInvalid(token)
				.map(Claims::getSubject)
				.orElseThrow(
					() -> new UsernameNotFoundException(ERROR_MESSAGE)
				);
		} catch (ExpiredJwtException e) {
			log.debug("만료된 JWT에서 sbj 추출: {}", e.getMessage());
			return e.getClaims().getSubject();
		}

	}

}
