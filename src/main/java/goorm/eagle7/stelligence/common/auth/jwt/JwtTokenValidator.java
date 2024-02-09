package goorm.eagle7.stelligence.common.auth.jwt;

import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

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
	 * <h2>토큰 유효성 검사, 예상한 예외 발생 시 null로 치환</h2>
	 * @param token 토큰
	 * @return Optional<Claims> 토큰이 유효하면 토큰의 클레임 반환, 유효하지 않으면 Optional.empty()
	 */
	public Optional<Claims> getClaimsOrNullIfInvalid(String token) {
		try {
			log.debug("토큰 유효성 검사 시작, Optional 반환 = {}", token);
			return
				Optional.of(getClaims(token));
		} catch (IllegalArgumentException e) {
			log.debug("토큰이 없습니다.");
		} catch (ExpiredJwtException e) {
			log.debug("만료된 토큰입니다.");
		} catch (MalformedJwtException e) {
			log.debug("토큰 값 형식이 잘못되었습니다.");
		} catch (UnsupportedJwtException e) {
			log.debug("예상하는 형식과 일치하지 않는 특정 형식이나 구성입니다.");
		} catch (JwtException e) {
			log.debug("유효하지 않은 토큰입니다.");
		}
		return Optional.empty();
	}

	/**
	 * <h2>Token token 검증 및 claims 추출</h2>
	 * <p>- 만료 검증: parseSignedClaims 이용, 만료 시간이 현재 시간보다 이전이면 만료된 토큰</p>
	 * <p>- 서명 검증: 데이터 해석, 클레임 추출, 서명 검증</p>
	 * @param token 검증 및 내용 추출할 토큰
	 * @return Claims 토큰의 클레임(페이로드) 반환
	 * @throws IllegalArgumentException 토큰 값이 없습니다.
	 * @throws ExpiredJwtException 만료된 토큰입니다.
	 * @throws MalformedJwtException 토큰 값 형식이 잘못되었습니다.
	 * @throws UnsupportedJwtException 예상하는 형식과 일치하지 않는 특정 형식이나 구성입니다.
	 * @throws JwtException 상기 셋 중 어느 것도 아닐 때, 유효하지 않은 토큰입니다.
	 */
	public Claims getClaims(String token) {
 		return Jwts.parser()
			.verifyWith(key) // 서명 검증 시 사용할 키
			.build()
			.parseSignedClaims(token) // 서명의 유효성 검증
			.getPayload();
	}
}
