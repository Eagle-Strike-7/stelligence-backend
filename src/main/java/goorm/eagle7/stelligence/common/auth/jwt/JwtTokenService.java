package goorm.eagle7.stelligence.common.auth.jwt;

import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
import goorm.eagle7.stelligence.domain.member.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JwtTokenService
 * <h2>JWT 토큰의 파싱과 검증</h2>
 * <p>- 토큰의 유효성 검증</p>
 * <p>- 토큰에서 사용자 정보 추출</p>
 * <p>log: 비즈니스 로직 상 error가 아니기 때문에 log.debug로 설정</p>
 *
 * getMemberId - token에서 sub(memberId) 추출
 * isTokenValidated - boolean 반환, token 유효성 검사
 * makeAuthenticationFromToken - token 정보에서 Authentication 만들어 반환
 * getTokenFromCookie - 유효한 쿠키에서 토큰 추출(null 포함 반환)
 * extractMemberInfo - token에서 MemberInfo로 조립
 * private validateTokenOrThrows - token 유효성 검사, 유효하지 않다면 throws
 * getMemberIdFromExpiredToken - 만료된 토큰에서 memberId 추출
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenService {

	private final JwtProperties jwtProperties;
	private final JwtTokenParser jwtTokenParser;
	private final JwtTokenValidator jwtTokenValidator;

	private static final String ERROR_MESSAGE = "유효하지 않은 사용자입니다.";

	/**
	 * <h2>token에서 sub(memberId) 추출</h2>
	 * @param claims token
	 * @return memberId 현재 로그인한 사용자의 memberId
	 * @throws BadJwtException 유효하지 않은 사용자입니다.
	 */
	public Long getMemberId(Claims claims) {
		String subject = jwtTokenParser.getSubject(claims);
		return Long.parseLong(subject);
	}

	/**
	 * <h2>token 유효성 검사</h2>
	 * <p>- token이 null, empty, blank인지 확인</p>
	 * <p>- 서명 검증 및 만료 검증</p>
	 * @param token 검사할 token
	 * @return boolean 유효한 토큰이면 true, 아니면 false
	 */
	public boolean isTokenValidated(String token) {
		try {
			validateTokenOrThrows(token);
			return true;
		} catch (JwtException | UsernameNotFoundException |
				 ExpiredJwtException | IllegalArgumentException e) {
			return false;
		}
	}

	/**
	 * <h2>token 정보에서 Authentication 만들어 반환</h2>
	 * @param accessToken 추출할 token
	 * @return Authentication token 정보로 만든 Authentication
	 */
	public Authentication makeAuthenticationFrom(String accessToken) {

		Claims claims = jwtTokenParser.getClaims(accessToken)
			.orElseThrow(
				() -> {
					log.debug("authentication 생성 실패, 확인 필요.");
					return new UsernameNotFoundException(ERROR_MESSAGE);
				}
			);
		MemberInfo memberInfo = extractMemberInfo(claims);

		UserDetails user = User.builder()
			.username(memberInfo.getId().toString())
			.password("")
			.authorities(memberInfo.getRole().getLabel())
			.build();
		log.debug("user: {}", user);
		return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());

	}

	/**
	 * <h2>쿠키에서 토큰 추출(null 포함)</h2>
	 * @param cookie 유효한 쿠키
	 * @return token 쿠키에서 추출한 토큰, 없다면 null 반환
	 */
	public String getTokenFromCookie(Cookie cookie) {

		if (cookie == null) {
			log.debug("쿠키가 없습니다.");

			return null;
		}
		return cookie.getValue();

	}

	/**
	 * <h2>token에서 MemberInfo로 조립</h2>
	 * @param claims 추출할 token
	 * @return MemberInfo memberId, role
	 */
	public MemberInfo extractMemberInfo(Claims claims) {

		// accessToken이 5분 이내면 refresh 토큰으로 accessToken 재발급
		// accessToken이 만료되었을 때, memberId를 추출하기 위해 사용
		// refreshToken 재발급도 여기서 진행
		String memberId = jwtTokenParser.getSubject(claims);
		Role role = jwtTokenParser.getRole(claims, jwtProperties.getClaims().getRole());

		return MemberInfo.of(
			Long.parseLong(memberId),
			role);

	}

	/**
	 * <h2>token 유효성 검사, 유효하지 않다면 throws</h2>
	 * @param token 검사할 token
	 * @throws JwtException 유효하지 않은 사용자입니다.
	 * @throws IllegalArgumentException 토큰 값이 없습니다.
	 */
	public Optional<Claims> validateTokenOrThrows(String token) {
		return jwtTokenValidator.getClaimsOrNullIfInvalid(token);
	}

	// 하기 메서드는 dev에서 사용

	/**
	 * <h2>만료된 토큰에서 memberId 추출</h2>
	 * @param token 만료된 토큰
	 * @return String subject(memberId)
	 */
	public String getMemberIdFromExpiredToken(String token) {
		return jwtTokenParser.extractSubFromExpiredToken(token);
	}

}
