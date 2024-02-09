package goorm.eagle7.stelligence.common.auth.jwt;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.KeyException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
class JwtTokenValidatorTest {

	// 기준 SecretKey
	private SecretKey stdSecretKey;

	// 같은 알고리즘, 다른 길이 SecretKey
	private SecretKey differentAlgoLengthSecretKey;

	// 다른 알고리즘 SecretKey
	private PrivateKey differentAlgorithmSecretKey;

	// 다른 문자로 생성된 SecretKey
	private SecretKey invalidValueSecretKey;

	// 기준 JwtBuilder
	private JwtBuilder stdJwtBuilder;

	@InjectMocks
	private JwtTokenValidator jwtTokenValidator;

	@BeforeEach
	void setUp() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {

		// SecretKey에 사용할 문자 생성
		byte[] decodedKey = Base64.getDecoder().decode(
			"jwtSecretsafasfjans345344kfnaskjfowefoiwnefoinwofn03844h8204h20hf820fdnfnf934534+345H4");
		// 기준 SecretKey 생성
		stdSecretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
		// 같은 알고리즘, 다른 길이 SecretKey 생성
		differentAlgoLengthSecretKey = new SecretKeySpec(decodedKey, 0,
			decodedKey.length, "HmacSHA512");

		// 다른 문자로 생성된 SecretKey 생성
		byte[] invalidValueDecodedKey = Base64.getDecoder().decode("jwtSecretsafasfjanskfnaskdnfoaenf934534+345");
		invalidValueSecretKey = new SecretKeySpec(
			invalidValueDecodedKey, 0,
			invalidValueDecodedKey.length, "HmacSHA256");

		// 다른 알고리즘 SecretKey 생성
		// ECDSA 키 쌍 생성
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
		ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp521r1");
		keyPairGenerator.initialize(ecSpec, new SecureRandom());
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		differentAlgorithmSecretKey = keyPair.getPrivate();

		// 기준 JwtBuilder 생성
		stdJwtBuilder = Jwts.builder()
			.header()
			.add("alg", "HS256")
			.add("typ", "JWT")
			.and()
			.issuedAt(new Date(System.currentTimeMillis()));
		
		// 테스트할 jwtTokenValidator 생성, 기준 SecretKey 사용
		jwtTokenValidator = new JwtTokenValidator(stdSecretKey);

	}

	/**
	 * <h2>토큰이 유효한 경우 검증</h2>
	 * <p>검증 방식: claim 내용, 만료 시간 확인</p>
	 * <p>결과: 추출 성공, 만료 시간 이전 확인</p>
	 */
	@Test
	@DisplayName("[성공] - 토큰이 유효한 경우 추출 성공 - getClaims")
	void getClaimsToken() {

		// given
		String validToken = stdJwtBuilder
			.subject("1")
			.expiration(new Date(System.currentTimeMillis() + 1000))
			.claim("hi", "nice to meet you")
			.signWith(stdSecretKey)
			.compact();
		log.info("validToken: {}", validToken);

		// when
		Claims claims = jwtTokenValidator.getClaims(validToken);

		// then
		assertThat(claims.getSubject()).isEqualTo("1");
		assertThat(claims).containsEntry("hi", "nice to meet you");
		assertThat(claims.getExpiration()).isAfter(new Date(System.currentTimeMillis()));

	}

	/**
	 * <h2>토큰이 만료된 경우 검증</h2>
	 * <p>검증 방식: 예외 종류 및 기본 메시지 확인</p>
	 * <p>결과: ExpiredJwtException 예외 발생</p>
	 */
	@Test
	@DisplayName("[예외] - 토큰이 만료된 경우, ExpiredJwtException - getClaims")
	void getClaimsExpiredToken() {

		// given
		Date expiredDate = new Date(System.currentTimeMillis() - 1);
		String expiredToken = stdJwtBuilder
			.expiration(expiredDate)
			.signWith(stdSecretKey)
			.compact();
		log.info("expiredToken: {}", expiredToken);

		// then
		assertThatThrownBy(
			() -> jwtTokenValidator.getClaims(expiredToken))
			.isInstanceOf(ExpiredJwtException.class)
			.hasMessageStartingWith("JWT expired");

	}

	/**
	 * <h2>토큰이 null, 토큰 값이 blank, empty인 경우 검증</h2>
	 * <p>검증 방식: IllegalArgumentException 예외 및 메시지 확인</p>
	 * <p>결과: IllegalArgumentException 예외 발생</p>
	 */
	@Test
	@DisplayName("[예외] - 토큰이 null, 토큰 값이 blank, empty인 경우, "
		+ "IllegalArgumentException - getClaims")
	void getClaimsInvalidToken() {

		// given

		// when, then
		assertThatThrownBy(() -> jwtTokenValidator.getClaims(null))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("CharSequence cannot be null or empty.");
		assertThatThrownBy(() -> jwtTokenValidator.getClaims(" "))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("CharSequence cannot be null or empty.");
		assertThatThrownBy(() -> jwtTokenValidator.getClaims(""))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("CharSequence cannot be null or empty.");

	}

	/**
	 * <h2>토큰 형식이 일치하지 않는 경우 검증</h2>
	 * <p>- 대부분 token은 ey로 시작함.</p>
	 * <p>검증 방식: MalformedJwtException 예외 및 메시지 확인</p>
	 * <p>결과: MalformedJwtException 예외 발생</p>
	 */
	@Test
	@DisplayName("[예외] - 토큰이 형식(ey-)이 일치하지 않는 경우, MalformedJwtException - getCliams")
	void getClaimsInvalidFormatEyToken() {

		// given
		String invalidFormatToken = "yJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJicGF3YW4iLCJyb2xlIjpudWxsLCJlbmFibGUiOm51bGwsImV4cCI6MTUzNDgyMzUyNSwiaWF0IjoxNTM0Nzk0NzI1fQ.65PPknMebR53ykLm-EBIunjFJvlV-vL-pfTOtbBLtnQ";
		log.info("invalidFormatToken: {}", invalidFormatToken);

		// when, then
		assertThatThrownBy(
			() -> jwtTokenValidator.getClaims(invalidFormatToken))
			.isInstanceOf(MalformedJwtException.class);

	}

	/**
	 * <h2>토큰 형식이 일치하지 않는 경우 검증</h2>
	 * <p>- token은 header.payload.signature로 구성됨.</p>
	 * <p>검증 방식: MalformedJwtException 예외 및 메시지 확인</p>
	 * <p>결과: MalformedJwtException 예외 발생</p>
	 */
	@Test
	@DisplayName("[예외] - 토큰이 형식(a.b.c)이 일치하지 않는 경우, MalformedJwtException - getCliams")
	void getClaimsInvalidFormatDotToken() {

		// given
		String invalidFormatToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJicGF3YW4iLCJyb2xlIjpudWxsLCJlbmFibGUiOm51bGwsImV4cCI6MTUzNDgyMzUyNSwiaWF0IjoxNTM0Nzk0NzI1fQ";
		log.info("invalidFormatToken: {}", invalidFormatToken);

		// when, then
		assertThatThrownBy(
			() -> jwtTokenValidator.getClaims(invalidFormatToken))
			.isInstanceOf(MalformedJwtException.class);

	}

	/**
	 * <h2>SecretKey가 일치하지 않는 경우 검증</h2>
	 * <p>검증 방식: SignatureException 예외 및 메시지 확인</p>
	 * <p>결과: SignatureException 예외 발생</p>
	 */
	@Test
	@DisplayName("[예외] - 토큰 시크릿 키가 일치하지 않는 경우, SignatureException - getClaims")
	void getClaimsInvalidAlgorithmToken() {

		// given - 다른 SecretKey로 서명한 토큰 생성
		String invalidSecretKeyToken = stdJwtBuilder
			.signWith(invalidValueSecretKey)
			.compact();
		log.info("invalidSecretKeyToken: {}", invalidSecretKeyToken);

		// when, then
		assertThatThrownBy(
			() -> jwtTokenValidator.getClaims(invalidSecretKeyToken))
			.isInstanceOf(SignatureException.class)
			.hasMessage("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.");

	}

	/**
	 * <h2>토큰에 사용된 알고리즘이 일치하지 않는 경우 검증</h2>
	 * <p>검증 방식: UnsupportedJwtException 예외 및 메시지 확인</p>
	 * <p>결과: UnsupportedJwtException 예외 발생</p>
	 */
	@Test
	@DisplayName("[예외] - 토큰에 사용된 알고리즘이 일치하지 않는 경우, UnsupportedJwtException - getClaims")
	void getClaimsInvalidValueToken() {

		// given - 다른 알고리즘 SecretKey로 서명한 토큰 생성
		String differentAlgoLengthToken = stdJwtBuilder
			.signWith(differentAlgorithmSecretKey)
			.compact();
		log.info("differentAlgoLengthToken: {}", differentAlgoLengthToken);

		//then
		assertThatThrownBy(
			() -> jwtTokenValidator.getClaims(differentAlgoLengthToken))
			.isInstanceOf(UnsupportedJwtException.class);

	}

	/**
	 * <h2>토큰에 사용된 알고리즘이 길이만 일치하지 않는 경우 검증</h2>
	 * <p>검증 방식: 토큰에 넣은 값 추출해 확인</p>
	 * <p>결과: 성공</p>
	 */
	@Test
	@DisplayName("[확인] - 토큰에 사용된 알고리즘 길이만 일치하지 않는 경우, 성공 처리 - getClaims")
	void getClaimsOrNullIfDifferAlgoLengthToken() {

		// given
		String validToken = stdJwtBuilder
			.subject("100")
			.expiration(
				new Date(System.currentTimeMillis() + 1000))
			.claim("hi", "nice to meet you")
			.signWith(differentAlgoLengthSecretKey)
			.compact();
		log.info("validToken: {}", validToken);

		// when
		Claims claims = jwtTokenValidator.getClaims(validToken);

		//then
		assertThat(claims.getSubject()).isEqualTo("100");
		assertThat(claims.getExpiration()).isAfter(new Date(System.currentTimeMillis()));
		assertThat(claims).containsEntry("hi", "nice to meet you");

	}

	/**
	 * <h2>토큰이 유효한 경우, Claim 반환 확인</h2>
	 * <p>- 유효한 토큰을 받아 getClaims에서 Claim을 반환받은 경우, 그대로 반환하는지 확인</p>
	 * <p>검증 방식: claim 내용, 만료 시간 확인</p>
	 * <p>결과: 추출 성공, 만료 시간 이전 확인</p>
	 */
	@Test
	@DisplayName("[성공] - 토큰이 유효한 경우 - getClaimsOrNullIfInvalid")
	void getClaimsOrNullIfInvalid() {

		// given
		String validToken = "validToken";

		// 메서드 부분 모킹이 필요해 spy 사용.
		jwtTokenValidator = Mockito.spy(new JwtTokenValidator(stdSecretKey));

		// mock claims 생성 및 반환 설정
		Claims mockClaims = mock(Claims.class);
		when(mockClaims.getSubject()).thenReturn("1");
		when(mockClaims.get("hi")).thenReturn("nice to meet you");
		doReturn(mockClaims).when(jwtTokenValidator).getClaims(validToken);

		// when
		Optional<Claims> claims = jwtTokenValidator.getClaimsOrNullIfInvalid(validToken);

		// then
		assertThat(claims).isPresent();
		assertThat(claims.get().getSubject()).isEqualTo("1");
		assertThat(claims.get().get("hi")).isEqualTo("nice to meet you");

	}

	/**
	 * <h2>catch에 구체적으로 명시하지 않은 JwtEx인 경우, empty 반환 확인</h2>
	 * <p>- 해당 테스트를 통과하면, 명시한 JwtEx는 empty 반환한다 가정</p>
	 * <p>검증 방식: 임의의 JwtEx 발생시킨 후 empty인지 확인</p>
	 * <p>결과: empty 반환</p>
	 */
	@Test
	@DisplayName("[예외] - catch에 명시해 놓지 않은 JwtException 예외가 발생한 경우, empty 반환 - getClaimsOrNullIfInvalid")
	void getClaimsOrNullIfInvalidExpiredToken() {

		// given

		// 메서드 부분 모킹이 필요해 spy 사용.
		jwtTokenValidator = Mockito.spy(new JwtTokenValidator(stdSecretKey));
		// 그냥 진행하면, spy가 아닌 실제 객체로 생성돼 예외 조작 어려움.
		// jwtTokenValidator = new JwtTokenValidator(stdSecretKey);
		String expiredToken = "expiredToken";

		doThrow(new KeyException("유효하지 않은 토큰입니다.")).when(jwtTokenValidator).getClaims(expiredToken);

		// when
		Optional<Claims> result = jwtTokenValidator.getClaimsOrNullIfInvalid(expiredToken);

		// then - empty 반환
		assertThat(result).isEmpty();

	}

	/**
	 * <h2>IllegalArgumentException 예외가 발생한 경우, empty 반환 확인</h2>
	 * <p>검증 방식: IllegalArgumentException 예외 발생시킨 후 empty인지 확인</p>
	 * <p>결과: empty 반환</p>
	 */
	@Test
	@DisplayName("[예외] - IllegalArgumentException 예외가 발생한 경우, empty 반환 - getClaimsOrNullIfInvalid")
	void getClaimsOrNullIfInvalidIllegalToken() {

		// given
		String illegalToken = "illegalToken";

		// 메서드 부분 모킹이 필요해 spy 사용.
		jwtTokenValidator = Mockito.spy(new JwtTokenValidator(stdSecretKey));

		// IllegalArgumentException 예외 발생
		doThrow(new IllegalArgumentException("토큰 값이 없습니다.")).when(jwtTokenValidator).getClaims(illegalToken);

		// when
		Optional<Claims> result = jwtTokenValidator.getClaimsOrNullIfInvalid(illegalToken);

		// then - empty 반환
		assertThat(result).isEmpty();

	}

	/**
	 * <h2>JwtEx, Illigal 제외한 Ex이 발생한 경우 처리</h2>
	 * <p>검증 방식: Runtime 에러 발생한다면 그대로 에러 발생하는지, 메시지와 함께 확인</p>
	 * <p>결과: empty 반환이 아닌 RuntimeEx 발생</p>
	 */
	@Test
	@DisplayName("[예외] - 토큰에서 이외의 예외가 발생한 경우, 예외 발생 확인 - getClaimsOrNullIfInvalid")
	void getClaimsOrNullIfInvalidOtherException() {

		// given
		String illegalToken = "이상한 토큰";

		// 메서드 부분 모킹이 필요해 spy 사용.
		jwtTokenValidator = Mockito.spy(new JwtTokenValidator(stdSecretKey));

		// when - RuntimeException 예외 발생
		doThrow(new RuntimeException()).when(jwtTokenValidator).getClaims(illegalToken);

		// then - RuntimeException 예외 발생, 메시지 null
		assertThatThrownBy(
			() -> jwtTokenValidator.getClaimsOrNullIfInvalid(illegalToken))
			.isInstanceOf(RuntimeException.class)
			.hasMessage(null);

	}

}