package goorm.eagle7.stelligence.common.auth.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.member.model.Role;
import goorm.eagle7.stelligence.domain.member.model.SocialType;
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
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	private final SecretKey key;
	private static final String TYPE = "typ";
	private static final String ALGORITHM = "alg";
	private static final String TOKEN_TYPE = "JWT"; // 토큰의 유형
	private static final String ALGOTITHM_TYPE = "HS256"; // 해시 알고리즘
	private static final int ACCESS_TOKEN_EXPIRED_TIME = 1000 * 60 * 60; //1시간
	private static final int REFRESH_TOKEN_EXPIRED_TIME = 1000 * 60 * 60 * 24 * 7 * 2; //2주
	private static final String CLAIM_ROLE = "role"; // 사용자 정의 claim, ROLE
	private static final String CLAIM_ROLE_USER = Role.USER.toString();

	/**
	 * AccessToken 생성
	 * AccessToken은 주로 인증에 사용되는 토큰, 일반적으로 짧은 유효기간을 가지며, 사용자 식별, 주요 인증 정보(사용자의 권한, 역할, 기타 접근 제어에 필요한 정보)를 포함
	 * AccessToken은 서버에서 발급하고, 서버에 요청할 때마다 AccessToken을 함께 전달해 서버에 부담을 줄 수 있음.
	 * header, payload, signature
	 * - header: 토큰의 유형과 해시 알고리즘(type:JWT, alg: HS256)
	 * - payload: 토큰에 담을 정보(claim)
	 * 		- subject: 사용자를 식별하는 값 (memberId)
	 * 		- issuedAt: 토큰 발급 시간
	 * 		- expiration: 토큰 만료 시간
	 * 		- claim: 사용자 정의 claim role, user 추가
	 * 		- 이 외에도 사용자 정의 claim을 추가해 사용자의 권한, 역할, 기타 접근 제어에 필요한 정보를 포함할 수 있음
	 * - signature: 토큰의 유효성 검증을 위한 암호화된 문자열(SecretKey)
	 * @param memberId 사용자 식별자
	 * @return String AccessToken
	 */
	public String createAccessToken(Long memberId) {
		Date now = new Date();

		return Jwts.builder()
			.header() //header 설정
			.add(TYPE, TOKEN_TYPE) // 토큰의 유형, JWT
			.add(ALGORITHM, ALGOTITHM_TYPE) // 해시 알고리즘, HS256
			.and()
			// header 끝, 페이로드 시작
			.subject(memberId.toString()) // 토큰이 나타내는 주제나 사용자를 식별하는 값, 토큰 해석 시 참조
			.issuedAt(now) // 토큰이 발급된 시간, 토큰의 유효성을 검증할 때 사용
			.expiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRED_TIME)) // 토큰의 만료 시간, 현재 1시간
			.claim(CLAIM_ROLE,CLAIM_ROLE_USER) // 사용자 정의 claim 추가, 사용자의 권한, 역할, 기타 접근 제어에 필요한 정보를 포함할 수 있음
			// payload 끝, signature 시작
			.signWith(key) // signature 설정,  JWT를 서명하는 데 사용할 키를 지정, 서명 과정은 토큰의 무결성과 인증을 보장하는 데 중요, 헤더와 페이로드를 기반으로 생성됨
			.compact(); // 헤더, 페이로드, 서명 설정 완료, 압축되어 String으로 반환

	}

	/**
	 * 요구 사항에 맞춰 SocialType Token 생성, accessToken과 그 구조 같음.
	 * @param socialType SocialType(카카오, 네이버, 구글)
	 * @return String SocialType Token
	 */
	public String createSocialTypeToken(SocialType socialType) {
		Date now = new Date();

		return Jwts.builder()
			.header() //header 설정
			.add(TYPE, TOKEN_TYPE) // 토큰의 유형, JWT
			.add(ALGORITHM, ALGOTITHM_TYPE) // 해시 알고리즘, HS256
			.and()
			// header 끝, 페이로드 시작
			.subject(socialType.toString()) // 토큰이 나타내는 주제나 사용자를 식별하는 값, 토큰 해석 시 참조
			.issuedAt(now) // 토큰이 발급된 시간, 토큰의 유효성을 검증할 때 사용
			.expiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRED_TIME)) // 토큰의 만료 시간, 현재 1시간
			.claim(CLAIM_ROLE,CLAIM_ROLE_USER) // 사용자 정의 claim 추가, 사용자의 권한, 역할, 기타 접근 제어에 필요한 정보를 포함할 수 있음
			// payload 끝, signature 시작
			.signWith(key) // signature 설정,  JWT를 서명하는 데 사용할 키를 지정, 서명 과정은 토큰의 무결성과 인증을 보장하는 데 중요, 헤더와 페이로드를 기반으로 생성됨
			.compact(); // 헤더, 페이로드, 서명 설정 완료, 압축되어 String으로 반환

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
		Date now = new Date();

		return Jwts.builder()
			.header() //header 설정
			.add(TYPE, TOKEN_TYPE) // 토큰의 유형, JWT
			.add(ALGORITHM, ALGOTITHM_TYPE) // 해시 알고리즘, HS256
			.and()
			// header 끝, 페이로드 시작
			.subject(memberId.toString()) // 토큰이 나타내는 주제나 사용자를 식별하는 값, 토큰 해석 시 참조
			.issuedAt(now) // 토큰이 발급된 시간, 토큰의 유효성을 검증할 때 사용
			.expiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRED_TIME)) // 토큰의 만료 시간, 현재 2주
			// payload 끝, signature 시작
			.signWith(key) // signature 설정,  JWT를 서명하는 데 사용할 키를 지정, 서명 과정은 토큰의 무결성과 인증을 보장하는 데 중요, 헤더와 페이로드를 기반으로 생성됨
			.compact(); // 헤더, 페이로드, 서명 설정 완료, 압축되어 String으로 반환
	}

}
