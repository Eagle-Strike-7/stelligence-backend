package goorm.eagle7.stelligence.common.auth;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import goorm.eagle7.stelligence.api.exception.BaseException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

	// TODO 발급과 해석, 검증을 따로 분리~!

	private final SecretKey key;

	/**
	 * 메서드 요약:
	 * - 서블릿 환경에서 현재 스레드와 관련된 HTTP 요청 데이터에 접근해 HTTP 요청 헤더의 "Authorization" 헤더 값(JWT)을 추출
	 * 자세한 설명:
	 * RequestContextHolder: 현재 실행 중인 스레드와 관련된 HTTP 요청 데이터에 접근 가능
	 * currentRequestAttributes() 메서드: 현재 요청과 관련된 RequestAttributes 객체를 반환, 주로 HTTP 요청에 대한 정보를 포함함.
	 * 반환된 RequestAttributes 객체를  ServletRequestAttributes로 캐스팅하는 건 서블릿 환경에서 실행되는 경우에만 안전, 비-서블릿 환경에서는 ClassCastException을 발생시킬 수 있음.
	 * getRequest() 메서드: 현재 HTTP 요청에 해당하는 HttpServletRequest 객체 반환
	 * getHeader("Authorization") 메서드: HTTP 요청 "Authorization" 헤더의 값을 반환
	 * @return Authorization 헤더 값
	 *
	 */
	public String getJwt() {
		return (
			(ServletRequestAttributes)
				RequestContextHolder.currentRequestAttributes()) // requestAttributes
			.getRequest() // HttpServletRequest
			.getHeader("Authorization");
	}

	/**
	 * AccessToken에서 memberId 추출
	 * 1. null or empty 검사
	 * 2. key 값으로 JWT 파싱(데이터 해석, 클레임 추출, 서명 검증)
	 * 3. subject에서 memberId 추출
	 * @return memberId 사용자 식별자
	 * @throws BaseException 유효성 검사, JWT 파싱 실패인 경우.
	 */
	public Long getMemberId(String accessToken) throws BaseException {

		// 1. accessToken 유효성 검사 (null or empty 조사)
		if (!StringUtils.hasText(accessToken)) {
			throw new BaseException("JWT가 없습니다.");
		}

		// 2. JWT 파싱
		Jws<Claims> claims; // 서명이 포함된 클레임
		try {
			claims = Jwts.parser()
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

		// 3. subject == memberId, memberId 추출
		return Long.parseLong(claims.getPayload().getSubject());

	}

}