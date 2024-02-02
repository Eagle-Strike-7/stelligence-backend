package goorm.eagle7.stelligence.common.auth.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
import goorm.eagle7.stelligence.domain.member.model.Role;
import io.jsonwebtoken.Claims;
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

	private final JwtTokenValidator jwtTokenValidator;
	private final JwtTokenParser jwtTokenParser;

	@Value("${http.header.field}")
	private String authorization;
	@Value("${jwt.claim.role}")
	private String claimRole;
	private static final String ERROR_MESSAGE = "유효하지 않은 사용자입니다.";

	/**
	 * <h2>token에서 sub(memberId) 추출</h2>
	 * @param token token
	 * @return memberId 현재 로그인한 사용자의 memberId
	 * @throws BadJwtException 유효하지 않은 사용자입니다.
	 */
	public Long getMemberId(String token) {
		String subject = jwtTokenParser.getSubject(token);
		return Long.parseLong(subject);
	}

	/**
	 * token의 유효성 검사, 유효하지 않다면 throw BadJwtException
	 * @param token 검사할 token
	 *              - null, empty, blank 검사
	 *              - 서명 검증 및 만료 검증
	 * @throws BadJwtException 유효하지 않은 사용자입니다.
	 */
	public void validateTokenOrThrows(String token) {
		jwtTokenValidator.validateAndExtractClaims(token);
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
			jwtTokenValidator.validateAndExtractClaims(token);
			return true;
		} catch (JwtException e) {
			return false;
		}
	}

	/**
	 * token 정보에서 Authentication 만들어 반환
	 * @param token token
	 * @return Authentication token 정보에서 만든 Authentication
	 * @throws BadJwtException 유효하지 않은 사용자입니다.
	 */
	public Authentication makeAuthenticationFromToken(String token) {

		Claims claims = jwtTokenValidator.validateAndExtractClaims(token);

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

	// 하기 메서드는 dev에서 사용

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
	 * <h2>만료된 토큰에서 memberId 추출</h2>
	 * @param token 만료된 토큰
	 * @return String subject(memberId)
	 */
	public String getMemberIdFromExpiredToken(String token) {
		return jwtTokenParser.extractSubFromExpiredToken(token);
	}

	/**
	 * <h2>token에서 MemberInfo로 조립</h2>
	 * @param token 추출할 token
	 * @return MemberInfo memberId, role
	 */
	public MemberInfo extractMemberInfo(String token) {

		Claims claims = jwtTokenValidator.validateAndExtractClaims(token);
		return MemberInfo.of(
			Long.parseLong(claims.getSubject()),
			Role.getRoleFromString(claims.get(claimRole, String.class))); // TODO valueOf로 변경

	}

}