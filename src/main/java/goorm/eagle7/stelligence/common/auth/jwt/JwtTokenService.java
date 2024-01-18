package goorm.eagle7.stelligence.common.auth.jwt;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import goorm.eagle7.stelligence.api.exception.BaseException;
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
 *  - HttpServletRequest에서 JWT 추출
 *  - 예외 처리 및 로깅
 * log 수준: 비즈니스 로직 상 error가 아니기 때문에 log.debug로 설정
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

	/**
	 * token에서 MemberInfo 추출
	 * 1. JWT 파싱해 claim 추출
	 * 2. claim의 subject에서 MemberInfo 추출
	 * @param claims 서명이 포함된 클레임
	 * @return MemberInfo 사용자 정보(식별자, 권한)
	 * @throws BaseException 유효성 검사, JWT 파싱 실패인 경우.
	 */
	public MemberInfo getMemberInfo(Claims claims) throws BaseException {
		return extractMemberInfo(claims); // 클레임에서 사용자 정보 추출

	}


	/**
	 * claims에서 memberId 추출
	 * @param claims 서명이 포함된 클레임
	 * @return memberId
	 */

	public Long getMemberId (Claims claims) {
		return Long.parseLong(claims.getSubject());
	}

	/**
	 * token의 유효성 검사
	 * @param token 검사할 token
	 *              - null or empty 검사
	 *              - 서명 검증
	 *              - 만료 검증
	 * @return boolean 유효한 토큰이면 true, 아니면 false
	 *
	 */
	public boolean validateToken(String token) throws BaseException {

		return validateIsTokenExists(token) && (validateAndGetClaims(token) != null && (validateActiveToken(
			validateAndGetClaims(token))));
	}

	/**
	 * token의 null or empty 조사
	 * @param token 검사할 token
	 * @return boolean null or empty이면 true, 아니면 false
	 */
	public boolean validateIsTokenExists(String token) {
		// null or empty 조사
		return StringUtils.hasText(token);
	}


	/**
	 * 토큰에서 서명 검증 후 Claims 추출(파싱)
	 * 	- 파싱: 데이터 해석, 클레임 추출, 서명 검증
	 * @param token 토큰
	 * @return Claims 서명이 포함된 클레임, error 시 null 반환
	 * TODO Optional로 변경 고민
	 */
	public Claims validateAndGetClaims(String token) {

		try {
			return Jwts.parser()
				.verifyWith(key) // 서명 검증 시 사용할 키 정하기
				.build()
				.parseSignedClaims(token) // 서명의 유효성 검증
				.getPayload();
			// 발생 예: 서명 불일치, 잘못된 형식 등
			// 어떤 이유인지 확인 위해 log 추가, 어느 정도 확인 후 Exception으로 통일 예정
			// 비즈니스 로직 상 error가 아니기 때문에 log.debug로 설정
		} catch (ExpiredJwtException e) {
			log.debug("만료된 JWT: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			log.debug("잘못된 형식의 JWT: {}", e.getMessage());
		} catch (Exception e) {
			log.debug("JWT 파싱 실패: {}", e.getMessage());
		}
		return null;
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
	 * Token token 검증 - 현재 시간으로부터 만료 검증
	 * @param claims 서명이 포함된 클레임
	 * @return boolean 만료된 토큰이면 false, 유효한 토큰이면 true
	 */
	public boolean validateActiveToken(Claims claims) {
		try {
			return !claims
				.getExpiration().before(new Date());
		} catch (Exception e) {
			log.debug("JWT 만료 검증 실패: {}", e.getMessage());
			return false;
		}
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
			throw new BaseException("유효하지 않은 사용자입니다.");
		}
	}

	/**
	 * TODO cookies null or empty 조사
	 * 쿠키에서 토큰 추출
	 * @param request HttpServletRequest 객체
	 * @param cookieName 쿠키 이름
	 * @return token 쿠키에서 추출한 토큰, 없다면 null 반환
	 */
	public String extractJwtFromCookie(HttpServletRequest request, String cookieName) {

		return Arrays.stream(request.getCookies()).
			filter(
				tCookie -> tCookie.getName().equals(cookieName))
			.findAny()
			.map(Cookie::getValue)
			.orElse(null);
	}

	/**
	 * Claims에서 claim값 추출해 MemberInfo로 조립
	 * @param claims 서명이 포함된 클레임
	 * @return MemberInfo
	 */
	public MemberInfo extractMemberInfo(Claims claims) {
		try {
			return MemberInfo.of(
				Long.parseLong(claims.getSubject()),
				Role.getRoleFromString(claims.get(claimRole, String.class)));
		} catch (NumberFormatException e) {
			log.debug("JWT에 저장된 사용자 식별자가 올바르지 않습니다. {}", e.getMessage());
			throw new BaseException("유효하지 않은 사용자입니다.");
		}
	}

}