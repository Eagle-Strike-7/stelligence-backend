package goorm.eagle7.stelligence.common.auth.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
import goorm.eagle7.stelligence.domain.member.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
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
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenService {

	private final SecretKey key;
	private static final String AUTHORIZATION = "Authorization";
	private static final String CLAIM_ROLE = "role";

	/**
	 * AccessToken에서 MemberInfo 추출
	 * 1. 유효성 검사
	 * 2. JWT 파싱해 claim 추출
	 * 3. claim의 subject에서 memberId 추출
	 * @return MemberInfo 사용자 정보(식별자, 권한)
	 * @throws BaseException 유효성 검사, JWT 파싱 실패인 경우.
	 */
	public MemberInfo getMemberInfo(String accessToken) throws BaseException {

		validateActiveToken(accessToken); // 토큰 유효성 검사
		Jws<Claims> claims = parseToken(accessToken); // 서명이 포함된 클레임
		return extractMemberInfo(claims); // 클레임에서 사용자 정보 추출

	}

	/**
	 * Token token 검증 - null or empty 조사, 서명의 유효성, 만료 검증
	 * @param token 검증할 token(Bearer 접두어 제외)
	 * @return boolean 만료된 토큰이면 false, 유효한 토큰이면 true
	 */
	public boolean validateActiveToken(String token) {

		// null or empty 조사
		if (!StringUtils.hasText(token)) {
			throw new BaseException("JWT가 없습니다.");
		}

		try {
			return !Jwts.parser()
				.verifyWith(key) // 서명 검증 시 사용할 키 정하기
				.build()
				.parseSignedClaims(token) // 서명의 유효성 검증
				.getPayload() // 페이로드 반환
				.getExpiration().before(new Date()); // 토큰의 만료 시간이 현재 시간보다 이전인지 확인
		} catch (Exception e) {
			throw new BaseException("JWT 검증 실패"); // TODO 상세한 예외 처리?
		}
	}

	/**
	 *
	 * "Authorization"의 헤더 값에서 Bearer를 제외한 token 추출
	 * @param request HttpServletRequest 객체
	 * @return token
	 *
	 */
	public static String extractJwtFromHeader(HttpServletRequest request) {
		return JwtTokenUtil.removeBearerPrefix(
			request.getHeader(AUTHORIZATION));
	}

	public static String extractJwtFromCookie(HttpServletRequest request) {

		// TODO switch로 세 종류를 나눠서 구현?
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("jwt")) {
					return JwtTokenUtil.removeBearerPrefix(
						cookie.getValue());

				}
			}
		}
		return null;
	}

	/**
	 * 토큰에서 클레임 추출(파싱)
	 * 	- 파싱: 데이터 해석, 클레임 추출, 서명 검증
	 * @param accessToken
	 * @return Jws<Claims> 서명이 포함된 클레임
	 */
	private Jws<Claims> parseToken(String accessToken) {

		try {
			return Jwts.parser()
				.verifyWith(key) // 서명 검증 시 사용할 키 정하기
				.build()
				.parseSignedClaims(accessToken); // 서명의 유효성 검증
			// 발생 예: 서명 불일치, 만료된 토큰, 잘못된 형식 등
			// 어떤 이유인지 확인 위해 log 추가, 어느 정도 확인 후 Exception으로 통일 예정
		} catch (ExpiredJwtException e) {
			log.error("만료된 JWT: {}", e.getMessage());
			throw new BaseException("만료된 JWT입니다.");
		} catch (MalformedJwtException e) {
			log.error("잘못된 형식의 JWT: {}", e.getMessage());
			throw new BaseException("잘못된 형식의 JWT입니다.");
		} catch (JwtException e) {
			log.error("JWT 파싱 실패: {}", e.getMessage());
			throw new BaseException("JWT 파싱에 실패했습니다.");
		} catch (Exception e) {
			log.error("JWT 파싱 실패", e);
			throw new BaseException("JWT 파싱에 실패했습니다.");
		}
	}

	/**
	 * Jws<Claims>에서 claim값 추출해 MemberInfo로 조립
	 * @param claims
	 * @return MemberInfo
	 */
	private MemberInfo extractMemberInfo(Jws<Claims> claims) {
		return MemberInfo.of(
			Long.parseLong(claims.getPayload().getSubject()),
			Role.getRoleFromString(claims.getPayload().get(CLAIM_ROLE, String.class)));
	}

}