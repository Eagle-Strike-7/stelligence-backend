package goorm.eagle7.stelligence.common.auth.jwt;

import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
class JwtTokenValidator {

	private final SecretKey key;

	/**
	 * <h2>토큰 유효성 검사, 예상한 예외 발생 시 null로 치환</h2>
	 * <p>- cookie를 사용하고 있어 token은 없으면 그대로 예외 발생</p>
	 * <p>- 만료된 토큰만 유효하게 empty 반환</p>
	 * @param token 토큰
	 * @return Optional<Claims> 토큰이 유효하면 토큰의 클레임 반환, 재발급 고려가 필요한 경우 Optional.empty() 반환
	 * @throws UsernameNotFoundException JwtEx, IllegalEx은 무조건 재로그인 필요
	 */
	public Optional<Claims> getClaimsOrNullIfInvalid(String token) {
		try {
			log.trace("토큰 유효성 검사 시작, Optional 반환 = {}", token);
			return Optional.of(getClaims(token));
		} catch (ExpiredJwtException e) {
			log.debug("만료된 토큰입니다. empty 반환");
			return Optional.empty();
		} catch (JwtException e) {
			log.debug("JwtException 발생, 유효하지 않은 토큰입니다.");
		} catch (IllegalArgumentException e) {
			log.debug("토큰 값이 없습니다.");
		}
		throw new UsernameNotFoundException("유효하지 않은 사용자입니다.");
	}

	/**
	 * <h2>Token token 검증 및 claims 추출</h2>
	 * <p>- 만료 검증: parseSignedClaims 이용, 만료 시간이 현재 시간보다 이전이면 만료된 토큰</p>
	 * <p>- 서명 검증: 데이터 해석, 클레임 추출, 서명 검증</p>
	 * @param token 검증 및 내용 추출할 토큰
	 * @return Claims 토큰의 클레임(페이로드) 반환
	 * @throws IllegalArgumentException 토큰 값이 없습니다.
	 * @throws ExpiredJwtException 만료된 토큰입니다.
	 * @throws JwtException Jwt 관련 Ex, 유효하지 않은 토큰입니다.
	 */
	public Claims getClaims(String token) {
		return Jwts.parser()
			.verifyWith(key) // 서명 검증 시 사용할 키
			.build()
			.parseSignedClaims(token) // 서명의 유효성 검증
			.getPayload();
	}

}
