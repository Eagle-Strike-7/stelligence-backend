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
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.KeyException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
class JwtTokenValidatorTest {

	private SecretKey stdSecretKey;
	private SecretKey differentAlgoLengthSecretKey;
	private PrivateKey invalidAlgorithmSecretKey;
	private SecretKey invalidValueSecretKey;

	private JwtBuilder stdJwtBuilder;

	@InjectMocks
	private JwtTokenValidator jwtTokenValidator;

	@BeforeEach
	void setUp() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {

		byte[] decodedKey = Base64.getDecoder().decode(
			"jwtSecretsafasfjans345344kfnaskjfowefoiwnefoinwofn03844h8204h20hf820fdnfnf934534+345H4");

		stdSecretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
		differentAlgoLengthSecretKey = new SecretKeySpec(decodedKey, 0,
			decodedKey.length, "HmacSHA512");

		byte[] invalidValueDecodedKey = Base64.getDecoder().decode("jwtSecretsafasfjanskfnaskdnfoaenf934534+345");
		invalidValueSecretKey = new SecretKeySpec(
			invalidValueDecodedKey, 0,
			invalidValueDecodedKey.length, "HmacSHA256");

		// ECDSA 키 쌍 생성
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
		ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp521r1");
		keyPairGenerator.initialize(ecSpec, new SecureRandom());
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		invalidAlgorithmSecretKey = keyPair.getPrivate();
		// invalidAlgorithmSecretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "RSA512");

		stdJwtBuilder = Jwts.builder()
			.header()
			.add("alg", "HS256")
			.add("typ", "JWT")
			.and()
			.issuedAt(new Date(System.currentTimeMillis()));
		jwtTokenValidator = new JwtTokenValidator(stdSecretKey);

	}

	@Test
	@DisplayName("[성공] - 토큰이 유효한 경우, 생성한 토큰이 유효한지 확인")
	void getClaimsTokenGene() {

		// given
		String validToken = stdJwtBuilder
			.subject("1")
			.expiration(new Date(System.currentTimeMillis() + 1000))
			.claim("role", "USER")
			.signWith(stdSecretKey)
			.compact();
		log.info("validToken: {}", validToken);

		// when
		Claims claims = jwtTokenValidator.getClaims(validToken);

		// then
		assertThat(claims.getSubject()).isEqualTo("1");
		assertThat(claims).containsEntry("role", "USER");

	}

	@Test
	@DisplayName("[예외] - 토큰이 만료된 경우, 예외 확인")
	void getClaimsExpiredTokenGene() {

		// given
		String expiredToken = stdJwtBuilder
			.subject("1")
			.expiration(
				new Date(System.currentTimeMillis() - 1))
			.claim("role", "USER")
			.signWith(stdSecretKey)
			.compact();
		log.info("expiredToken: {}", expiredToken);

		// then
		assertThatThrownBy(
			() -> jwtTokenValidator.getClaims(expiredToken))
			.isInstanceOf(ExpiredJwtException.class);

	}

	@Test
	@DisplayName("[예외] - 토큰이 null, 토큰 값이 blank, empty인 경우, 예외 확인")
	void getClaimsInvalidTokenGene() {

		// given

		// then
		assertThatThrownBy(() -> jwtTokenValidator.getClaims(null))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("토큰 값이 없습니다.");
		assertThatThrownBy(() -> jwtTokenValidator.getClaims(" "))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("토큰 값이 없습니다.");
		assertThatThrownBy(() -> jwtTokenValidator.getClaims(""))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("토큰 값이 없습니다.");

	}

	@Test
	@DisplayName("[예외] - 토큰이 형식(ey)이 일치하지 않는 경우, 예외 확인")
	void getClaimsInvalidFormatEyTokenGene() {

		// given

		String invalidFormatToken = "yJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJicGF3YW4iLCJyb2xlIjpudWxsLCJlbmFibGUiOm51bGwsImV4cCI6MTUzNDgyMzUyNSwiaWF0IjoxNTM0Nzk0NzI1fQ.65PPknMebR53ykLm-EBIunjFJvlV-vL-pfTOtbBLtnQ";
		log.info("invalidFormatToken: {}", invalidFormatToken);

		// then
		assertThatThrownBy(
			() -> jwtTokenValidator.getClaims(invalidFormatToken))
			.isInstanceOf(MalformedJwtException.class);

	}

	@Test
	@DisplayName("[예외] - 토큰이 형식(a.b.c)이 일치하지 않는 경우, 예외 확인")
	void getClaimsInvalidFormatDotTokenGene() {

		// given

		String invalidFormatToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJicGF3YW4iLCJyb2xlIjpudWxsLCJlbmFibGUiOm51bGwsImV4cCI6MTUzNDgyMzUyNSwiaWF0IjoxNTM0Nzk0NzI1fQ";
		log.info("invalidFormatToken: {}", invalidFormatToken);

		// then
		assertThatThrownBy(
			() -> jwtTokenValidator.getClaims(invalidFormatToken))
			.isInstanceOf(MalformedJwtException.class);

	}

	@Test
	@DisplayName("[예외] - 토큰 시크릿 키가 일치하지 않는 경우, 예외 확인")
	void getClaimsInvalidAlgorithmTokenGene() {

		// given
		String invalidSecretKeyToken = stdJwtBuilder
			.subject("1")
			.expiration(
				new Date(System.currentTimeMillis()
					+ 1000 * 60))
			.claim("role", "USER")
			.signWith(invalidValueSecretKey)
			.compact();
		log.info("invalidSecretKeyToken: {}", invalidSecretKeyToken);

		// then
		assertThatThrownBy(
			() -> jwtTokenValidator.getClaims(invalidSecretKeyToken))
			.isInstanceOf(SignatureException.class);

	}

	@Test
	@DisplayName("[성공] - 토큰에 사용된 알고리즘 길이만 일치하지 않는 경우, 성공 처리됨")
	void getClaimsOrNullIfDifferAlgoLengthTokenGene() {

		// given
		String validToken = stdJwtBuilder
			.header()
			.add("alg", "HSA512")
			.and()
			.subject("1")
			.expiration(
				new Date(System.currentTimeMillis()
					+ 1000 * 60))
			.claim("role", "USER")
			.signWith(differentAlgoLengthSecretKey)
			.compact();
		log.info("validToken: {}", validToken);

		// when
		Claims claims = jwtTokenValidator.getClaims(validToken);

		//then
		assertThat(claims.getSubject()).isEqualTo("1");
		assertThat(claims).containsEntry("role", "USER");

	}

	@Test
	@DisplayName("[예외] - 토큰에 사용된 알고리즘이 일치하지 않는 경우, 예외 확인")
	void getClaimsInvalidValueTokenGene() {

		// given
		// header의 alg 값과 signWith가 일치하지 않아도 생성 가능 -> 권장 X, 변경.
		String differentAlgoLengthToken = stdJwtBuilder
			.header()
			.add("alg", "HSA512")
			.and()
			.signWith(invalidAlgorithmSecretKey)
			.compact();
		log.info("differentAlgoLengthToken: {}", differentAlgoLengthToken);

		//then
		assertThatThrownBy(
			() -> jwtTokenValidator.getClaims(differentAlgoLengthToken))
			.isInstanceOf(UnsupportedJwtException.class);

	}

	@Test
	@DisplayName("[성공] - 토큰이 유효한 경우")
	void getClaimsOrNullIfInvalid() {

		// given
		String validToken = "validToken";
		Claims mockClaims = mock(Claims.class);
		when(mockClaims.getSubject()).thenReturn("1");
		when(mockClaims.get("role")).thenReturn("USER");

		try (MockedStatic<Jwts> mocked = mockStatic(Jwts.class)) {
			JwtParser parser = mock(JwtParser.class, RETURNS_DEEP_STUBS);
			when(parser.parseSignedClaims(anyString()).getPayload()).thenReturn(mockClaims);

			mocked.when(Jwts::parser).thenReturn(mock(JwtParserBuilder.class, RETURNS_DEEP_STUBS));
			mocked.when(() -> Jwts.parser().verifyWith(stdSecretKey).build()).thenReturn(parser);

			Optional<Claims> result = jwtTokenValidator.getClaimsOrNullIfInvalid(validToken);

			// then
			assertThat(result).isPresent();
			assertThat(result.get().getSubject()).isEqualTo("1");
			assertThat(result.get().get("role")).isEqualTo("USER");
			// assertThat(result.get()).containsEntry("role", "USER"); // 실패 TODO 확인 필요

		}

	}

	@Test
	@DisplayName("[예외] - 토큰에서 catch에 명시해 놓지 않은 JwtException 예외가 발생한 경우, empty 반환 - getClaimsOrNullIfInvalid")
	void getClaimsOrNullIfInvalidExpiredToken() {

		// given - 메서드 부분 모킹이 필요해 spy 사용.
		jwtTokenValidator = Mockito.spy(new JwtTokenValidator(stdSecretKey));
		// jwtTokenValidator = new JwtTokenValidator(stdSecretKey);
		String expiredToken = "expiredToken";

		doThrow(new KeyException("유효하지 않은 토큰입니다.")).when(jwtTokenValidator).getClaims(expiredToken);

		// when
		Optional<Claims> result = jwtTokenValidator.getClaimsOrNullIfInvalid(expiredToken);

		// then
		assertThat(result).isEmpty();

	}

	@Test
	@DisplayName("[예외] - 토큰에서 IllegalArgumentException 예외가 발생한 경우, empty 반환 - getClaimsOrNullIfInvalid")
	void getClaimsOrNullIfInvalidIllegalToken() {

		// given - 메서드 부분 모킹이 필요해 spy 사용.
		jwtTokenValidator = Mockito.spy(new JwtTokenValidator(stdSecretKey));

		// IllegalArgumentException 예외 발생
		String illegalToken = "illegalToken";
		doThrow(new IllegalArgumentException("토큰 값이 없습니다.")).when(jwtTokenValidator).getClaims(illegalToken);

		// when
		Optional<Claims> result = jwtTokenValidator.getClaimsOrNullIfInvalid(illegalToken);

		// then
		assertThat(result).isEmpty();

	}

	@Test
	@DisplayName("[예외] - 토큰에서 이외의 예외가 발생한 경우, 예외 발생 확인 - getClaimsOrNullIfInvalid")
	void getClaimsOrNullIfInvalidOtherException() {

		// given - 메서드 부분 모킹이 필요해 spy 사용.
		jwtTokenValidator = Mockito.spy(new JwtTokenValidator(stdSecretKey));

		// IllegalArgumentException 예외 발생
		String illegalToken = "이상한 토큰";
		doThrow(new RuntimeException("토큰 값이 없습니다.")).when(jwtTokenValidator).getClaims(illegalToken);

		// then
		assertThatThrownBy(
			() -> jwtTokenValidator.getClaimsOrNullIfInvalid(illegalToken))
			.isInstanceOf(RuntimeException.class);

	}

}