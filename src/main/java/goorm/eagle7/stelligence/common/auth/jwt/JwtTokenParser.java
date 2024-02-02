package goorm.eagle7.stelligence.common.auth.jwt;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.stereotype.Service;

import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
import goorm.eagle7.stelligence.domain.member.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// TODO extends JwtDefaultParser
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenParser {

	private final JwtTokenValidator jwtTokenValidator;

	private final SecretKey key;

	@Value("${http.header.field}")
	private String authorization;
	@Value("${jwt.claim.role}")
	private String claimRole;
	private static final String ERROR_MESSAGE = "유효하지 않은 사용자입니다.";

	/**
	 * token에서 sub(memberId) 추출
	 * @param token token
	 * @return memberId Long
	 * @throws BadJwtException 유효하지 않은 사용자입니다.
	 */
	public Long getSubject(String token) {
		Claims claims = jwtTokenValidator.validateAndExtractClaims(token);
		return Long.parseLong(claims.getSubject());
	}

	public MemberInfo extractMemberInfo(String token) {
		try {
			Claims claims = jwtTokenValidator.validateAndExtractClaims(token);
			return MemberInfo.of(
				Long.parseLong(claims.getSubject()),
				Role.getRoleFromString(claims.get(claimRole, String.class)));
		} catch (NumberFormatException e) {
			throw new BadJwtException("유효하지 않은 사용자입니다.");
		}
	}

	/**
	 * 만료된 토큰에서 subject(memberId) 추출
	 *  - 만료된 토큰은 claims null이기 때문에 Token으로만 받아야 함.
	 * @param token 만료된 토큰
	 * @return String subject(memberId)
	 */
	public String extractSubFromExpiredToken(String token) {

		try {
			return jwtTokenValidator.validateAndExtractClaims(token)
				.getSubject();
		} catch (ExpiredJwtException e) {
			log.debug("만료된 JWT에서 sbj 추출: {}", e.getMessage());
			return e.getClaims().getSubject();
		}
	}

}
