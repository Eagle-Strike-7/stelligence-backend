package goorm.eagle7.stelligence.common.auth.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
import goorm.eagle7.stelligence.domain.member.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JwtTokenService
 * 역할: JWT 토큰의 파싱과 검증
 * 책임:
 * 	- 토큰의 유효성 검증
 *  - 토큰에서 사용자 정보 추출
 *    - HttpServletRequest에서 JWT 추출 (개발 시만)
 * log: 비즈니스 로직 상 error가 아니기 때문에 log.debug로 설정
 *
 * getMemberId - token에서 sub(memberId) 추출
 * validateTokenOrThrows - void 반환(throw BadJwtException O)
 * isTokenValidated - boolean 반환(throw BadJwtException X)
 *  - validateExistsToken - token 자체가 null, empty, blank인지 확인
 *  - validateActiveToken - 만료 확인
 *  * getClaimsOrThrows - 코드 중복을 줄이기(private)
 * makeAuthenticationFromToken - token 정보에서 Authentication 만들어 반환
 * getTokenFromCookie - 유효한 쿠키에서 토큰 추출(null 포함 반환)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenService {

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
	public Long getMemberId(String token) {
		Claims claims = getClaimsOrThrows(token);
		return Long.parseLong(claims.getSubject());
	}

	/**
	 * token의 유효성 검사, 유효하지 않다면 throw BadJwtException
	 * @param token 검사할 token
	 *              - null, empty, blank 검사
	 *              - 서명 검증 및 만료 검증
	 * @throws BadJwtException 유효하지 않은 사용자입니다.
	 */
	public void validateTokenOrThrows(String token) {
		validateExistsToken(token);
		validateActiveToken(token);
	}

	/**
	 * token의 유효성 검사, 유효하지 않다면 false 반환
	 * @param token 검사할 token
	 *              - null, empty, blank 검사
	 * 	 *          - 서명 검증 및 만료 검증
	 * @return boolean 유효한 토큰이면 true, 아니면 false
	 *
	 */
	public boolean isTokenValidated(String token) {
		try {
			validateExistsToken(token);
			validateActiveToken(token);
			return true;
		} catch (BadJwtException e) {
			return false;
		}
	}

	/**
	 * token의 null or empty("") or blank(" ") 조사
	 * @param token 검사할 token
	 * @throws BadJwtException 유효하지 않은 사용자입니다.
	 */
	public void validateExistsToken(String token) {
		if (!StringUtils.hasText(token)) {
			throw new BadJwtException(ERROR_MESSAGE);
		}
	}

	/**
	 * 토큰에서 서명 검증 후 Claims 추출(파싱)
	 * 	- 파싱: 데이터 해석, 클레임 추출, 서명 검증
	 * @param token 토큰
	 * @return Claims 서명이 포함된 클레임
	 * @throws BadJwtException 유효하지 않은 사용자입니다.
	 */
	private Claims getClaimsOrThrows(String token) {

		try {
			return Jwts.parser()
				.verifyWith(key) // 서명 검증 시 사용할 키
				.build()
				.parseSignedClaims(token) // 서명의 유효성 검증
				.getPayload();
		} catch (ExpiredJwtException | MalformedJwtException e) {
			log.debug("validateAndGetClaims 만료된 토큰입니다. {}", e.getMessage());
			throw new BadJwtException(ERROR_MESSAGE);
		}
	}

	/**
	 * Token token 검증 - 현재 시간으로부터 만료 검증
	 * @param token 검증할 token
	 * @throws BadJwtException 유효하지 않은 사용자입니다.
	 */
	public void validateActiveToken(String token) {

		boolean expired = getClaimsOrThrows(token)
			.getExpiration().before(new Date());

		if (expired) {
			throw new BadJwtException(ERROR_MESSAGE);
		}
	}


	// 하기 메서드는 dev에서 사용
	/**
	 * token 정보에서 Authentication 만들어 반환
	 * @param token token
	 * @return Authentication token 정보에서 만든 Authentication
	 * @throws BadJwtException 유효하지 않은 사용자입니다.
	 */
	public Authentication makeAuthenticationFromToken(String token) {

		Claims claims = getClaimsOrThrows(token);

		UserDetails user = User.builder()
			.username(claims.getSubject())
			.password("")
			.authorities(Role.getRoleFromString(claims.get(claimRole, String.class)).getValue())
			.build();

		return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
	}

	/**
	 * 쿠키에서 토큰 추출(null 포함)
	 * @param cookie 유효한 쿠키
	 * @return token 쿠키에서 추출한 토큰, 없다면 null 반환
	 */
	public String getTokenFromCookie(Cookie cookie) {

		return cookie.getValue();

	}

	/**
	 * "Authorization"의 헤더 값에서 Bearer를 제외한 token 추출
	 * @param request HttpServletRequest 객체
	 * @return token Bearer 접두어 제외한 token
	 */
	public String extractJwtFromHeader(HttpServletRequest request) {
		try {
			return JwtTokenUtil.removeBearerPrefix(
				request.getHeader(authorization));
		} catch (Exception e) {
			log.debug("Authorization 헤더가 없거나 잘못된 형식입니다. {}", e.getMessage());
			throw new BadJwtException(ERROR_MESSAGE);
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
			return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
		} catch (ExpiredJwtException e) {
			log.debug("만료된 JWT에서 sbj 추출: {}", e.getMessage());
			return e.getClaims().getSubject();
		}
	}

	/**
	 * token에서 MemberInfo로 조립
	 * @param token token
	 * @return MemberInfo
	 * @throws BadJwtException 유효하지 않은 사용자입니다.
	 */
	public MemberInfo extractMemberInfo(String token) {
		try {
			Claims claims = getClaimsOrThrows(token);
			return MemberInfo.of(
				Long.parseLong(claims.getSubject()),
				Role.getRoleFromString(claims.get(claimRole, String.class)));
		} catch (NumberFormatException e) {
			log.debug("JWT에 저장된 사용자 식별자가 올바르지 않습니다. {}", e.getMessage());
			throw new BadJwtException(ERROR_MESSAGE);
		}
	}
}