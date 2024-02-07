package goorm.eagle7.stelligence.common.auth.jwt;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
class JwtTokenValidator {

	private final SecretKey key;

	/**
	 * <h2>token 유효성 검사</h2>
	 * @param token 검사할 token
	 * @return Claims 서명이 포함된 클레임 반환
	 * @throws ExpiredJwtException 만료된 토큰입니다.
	 * @throws MalformedJwtException 토큰 값 형식이 잘못되었습니다.
	 * @throws IllegalArgumentException 토큰 값이 없습니다.
	 * @throws UnsupportedJwtException 예상하는 형식과 일치하지 않는 특정 형식이나 구성입니다.
	 * @throws JwtException 상기 셋 중 어느 것도 아닐 때, 유효하지 않은 토큰입니다.
	 *
	 */ // TODO token null이면 어떤 error인지 확인 필요.
	public Claims validateAndExtractClaims(String token) {
		log.debug("토큰 유효성 검사 = {}", token);
		return getClaimsOrNullUnlessExpired(token);
	}

	/**
	 * <h2>Token 존재 여부 조사</h2>
	 * <p>null or empty("") or blank(" ") 조사</p>
	 * <p>검증 실패 시 ErrorMsg 통일.</p>
	 * @param token 검사할 token
	 * @return boolean token이 존재하면 true, 아니면 false
	 */
	private boolean isTokenExist(String token) {
		if (!StringUtils.hasText(token)) {
			log.debug("토큰 값이 없습니다.");
			return false;
		}
		return true;
	}

	/**
	 * <h2>Token token 검증 및 claims 추출(null 반환)</h2>
	 * <p>- 만료 검증: parseSignedClaims 이용, 만료 시간이 현재 시간보다 이전이면 만료된 토큰</p>
	 * <p>- 파싱: 데이터 해석, 클레임 추출, 서명 검증</p>
	 * <p>- 검증 실패 시 ErrorMsg 통일 및 null 반환</p>
	 * @param token 토큰
	 * @return Optional<Claims> 서명이 포함된 클레임, 해당 클레임에서 설정한 sub 등 정보 추출, error 발생 시 null 반환
	 * @throws ExpiredJwtException 만료된 토큰입니다.
	 * @throws MalformedJwtException 토큰 값 형식이 잘못되었습니다.
	 * @throws UnsupportedJwtException 예상하는 형식과 일치하지 않는 특정 형식이나 구성입니다.
	 * @throws JwtException 상기 셋 중 어느 것도 아닐 때, 유효하지 않은 토큰입니다.
	 */
	private Claims getClaimsOrNullUnlessExpired(String token) {
		return
			Jwts.parser()
				.verifyWith(key) // 서명 검증 시 사용할 키
				.build()
				.parseSignedClaims(token) // 서명의 유효성 검증
				.getPayload();
	}
}
