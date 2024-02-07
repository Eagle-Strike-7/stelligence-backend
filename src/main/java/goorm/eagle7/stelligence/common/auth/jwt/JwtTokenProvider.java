package goorm.eagle7.stelligence.common.auth.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

/**
 * JwtTokenProvider
 *  - Provider: 일반적으로 상태를 갖지 않으며, 서비스나 객체의 생성과 관리를 책임지는 객체
 * 역할: JWT 토큰의 생성 및 서명
 * 책임:
 * 	- JWT 액세스 토큰과 리프레시 토큰 생성
 * 	- 토큰의 기본 설정(예: 유효 시간, 클레임 등) 관리
 */
// TODO Date를 동시에 관리하는 법
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	private final SecretKey key;
	private final JwtProperties jwtProperties;

	/**
	 * AccessToken 생성
	 * AccessToken은 주로 인증에 사용되는 토큰, 일반적으로 짧은 유효기간을 가지며, 사용자 식별, 주요 인증 정보(사용자의 권한, 역할, 기타 접근 제어에 필요한 정보)를 포함
	 * AccessToken은 서버에서 발급하고, 서버에 요청할 때마다 AccessToken을 함께 전달해 서버에 부담을 줄 수 있음.
	 * header, payload, signature
	 * - header: 토큰의 유형과 해시 알고리즘(type:토큰 유형, alg: 알고리즘 종류)
	 * - payload: 토큰에 담을 정보(claim)
	 * 		- subject: 사용자를 식별하는 값 (memberId)
	 * 		- issuedAt: 토큰 발급 시간, 유효성 검증 시 사용 (현재 시각)
	 * 		- expiration: 토큰 만료 시간
	 * 		- claim: 사용자 정의 claim role, user 추가
	 * 		- 이 외에도 사용자 정의 claim을 추가해 사용자의 권한, 역할, 기타 접근 제어에 필요한 정보를 포함할 수 있음
	 * - signature: JWT를 서명하는 데 사용할 키 지정, 토큰의 유효성 검증을 위한 암호화된 문자열, 서명 과정은 토큰의 무결성과 인증을 보장하는 데 중요, 헤더와 페이로드를 기반으로 생성됨(SecretKey)
	 * - compact: 헤더, 페이로드, 서명을 하나의 문자열로 변환
	 *
	 * @param memberId 사용자 식별자
	 * @return String AccessToken
	 */
	public String createAccessToken(Long memberId) {
		Date now = new Date(System.currentTimeMillis());

		return Jwts.builder()
			.header()
			.add(jwtProperties.getHeader().getType(),
				jwtProperties.getHeader().getTokenType())
			.add(jwtProperties.getHeader().getAlgorithm(),
				jwtProperties.getHeader().getAlgorithm())
			.and() // header 끝, payload 시작
			.subject(memberId.toString())
			.issuedAt(now)
			.expiration(new Date(System.currentTimeMillis()
				+ jwtProperties.getAccessTokenExpirationMs()))
			.claim(jwtProperties.getClaim().getRole(), jwtProperties.getClaim().getValue())
			// payload 끝, signature 시작
			.signWith(key)
			.compact();

	}

	/**
	 * RefreshToken 생성
	 * header, payload, signature
	 * AccessToken과 유사하지만, 유효기간이 길고, 주로 재발급에 사용되는 토큰
	 * AccessToken이 만료되면 RefreshToken을 사용해 새로운 AccessToken을 발급
	 * RefreshToken은 claim에 사용자의 식별자만 포함하는 걸 권장.
	 * @param memberId 사용자 식별자
	 * @return String RefreshToken
	 */
	public String createRefreshToken(Long memberId) {
		Date now = new Date(System.currentTimeMillis());

		return Jwts.builder()
			.header()
			.add(jwtProperties.getHeader().getType(),
				jwtProperties.getHeader().getTokenType())
			.add(jwtProperties.getHeader().getAlgorithm(),
				jwtProperties.getHeader().getAlgorithm())
			.and()
			.subject(memberId.toString())
			.issuedAt(now)
			.expiration(new Date(System.currentTimeMillis() +
				jwtProperties.getRefreshTokenExpirationMs()))
			.signWith(key)
			.compact();
	}

	/**
	 * refreshToken 만료 시키기
	 *  - 만료 후 DB에 저장하기 위함.
	 * @param memberId 사용자 식별자
	 * @return String refreshToken
	 */
	public String expireRefreshToken(Long memberId) {
		Date now = new Date(System.currentTimeMillis());

		return Jwts.builder()
			.header()
			.add(jwtProperties.getHeader().getType(),
				jwtProperties.getHeader().getTokenType())
			.add(jwtProperties.getHeader().getAlgorithm(),
				jwtProperties.getHeader().getAlgorithm())
			.and()
			.subject(memberId.toString())
			.issuedAt(now)
			.expiration(now) // 만료 시간을 현재 시간으로 설정
			.signWith(key)
			.compact();
	}

}
