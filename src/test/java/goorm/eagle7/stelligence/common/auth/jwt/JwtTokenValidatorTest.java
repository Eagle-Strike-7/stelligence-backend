package goorm.eagle7.stelligence.common.auth.jwt;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import org.mockito.junit.jupiter.MockitoExtension;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;

@ExtendWith(MockitoExtension.class)
class JwtTokenValidatorTest {

	private SecretKey secretKey;
	private SecretKey invalidValueSecretKey;
	private SecretKey invalidAlgorithmSecretKey;
	private JwtBuilder jwtBuilder;

	@InjectMocks
	private JwtTokenValidator jwtTokenValidator;

	@BeforeEach
	void setUp() {

		byte[] decodedKey = Base64.getDecoder().decode("jwtSecretjofoianeofnwong94n34ns943+asadfas84");
		secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");

		byte[] invalidValueDecodedKey = Base64.getDecoder().decode("jwtSecretsafasfjanskfnaskdnfoaenf934534+345");
		invalidValueSecretKey = new SecretKeySpec(invalidValueDecodedKey, 0, invalidValueDecodedKey.length, "HmacSHA256");

		byte[] invalidAlgorithmDecodedKey = Base64.getDecoder().decode("jwtSecretjofoianeofnwong94n34ns943+asadfas84");
		invalidAlgorithmSecretKey = new SecretKeySpec(invalidAlgorithmDecodedKey, 0, invalidAlgorithmDecodedKey.length, "hmacsha256");

		jwtBuilder = Jwts.builder()
			.header()
			.add("alg", "HS256")
			.add("typ", "JWT")
			.and()
			.issuedAt(new Date(System.currentTimeMillis()))
			.signWith(secretKey);

	}

	@Test
	@DisplayName("[성공] - 토큰이 유효한 경우, 생성한 토큰이 유효한지 확인")
	void getClaimsOrNullIfInvalidTokenGene() {


		jwtTokenValidator = new JwtTokenValidator(secretKey);

		String validToken = jwtBuilder // header 끝, payload 시작
			.subject("1")
			.expiration(
				new Date(System.currentTimeMillis()
					+ 1000 * 60))
			.claim("role", "USER")
			.compact();

		Optional<Claims> result = jwtTokenValidator.getClaimsOrNullIfInvalid(validToken);

		assertThat(result.isPresent()).isTrue();
		assertThat(result.get().getSubject()).isEqualTo("1");
		assertThat(result.get().get("role")).isEqualTo("USER");

	}

	@Test
	@DisplayName("[예외] - 토큰이 만료된 경우, 예외 확인")
	void getClaimsOrNullIfInvalidExpiredTokenGene() {


		jwtTokenValidator = new JwtTokenValidator(secretKey);

		String expiredToken = jwtBuilder
			.subject("1")
			.expiration(
				new Date(System.currentTimeMillis() - 1))
			.claim("role", "USER")
			.compact();

		assertThrowsExactly(
			ExpiredJwtException.class,
			() -> jwtTokenValidator.getClaimsOrNullIfInvalid(expiredToken));

	}

	@Test
	@DisplayName("[예외] - 토큰이 null, 토큰 값이 blank, empty인 경우, 예외 확인")
	void getClaimsOrNullIfInvalidInvalidTokenGene() {

		jwtTokenValidator = new JwtTokenValidator(secretKey);

		// then
		assertThrowsExactly(
			IllegalArgumentException.class,
			() -> jwtTokenValidator.getClaimsOrNullIfInvalid(null));
		assertThrowsExactly(
			IllegalArgumentException.class,
			() -> jwtTokenValidator.getClaimsOrNullIfInvalid(""));
		assertThrowsExactly(
			IllegalArgumentException.class,
			() -> jwtTokenValidator.getClaimsOrNullIfInvalid(" "));

	}

	@Test
	@DisplayName("[예외] - 토큰 시크릿 키가 일치하지 않는 경우, 예외 확인")
	void getClaimsOrNullIfInvalidInvalidAlgorithmTokenGene() {

		jwtTokenValidator = new JwtTokenValidator(invalidValueSecretKey);

		// given
		String validToken = jwtBuilder
			.subject("1")
			.expiration(
				new Date(System.currentTimeMillis()
					+ 1000 * 60))
			.claim("role", "USER")
			.compact();

		assertThrowsExactly(
			SignatureException.class,
			() -> jwtTokenValidator.getClaimsOrNullIfInvalid(validToken));

	}

	@Test
	@DisplayName("[예외] - 토큰에 사용된 알고리즘이 일치하지 않는 경우, 예외 확인")
	void getClaimsOrNullIfInvalidInvalidValueTokenGene() {

		jwtTokenValidator = new JwtTokenValidator(invalidAlgorithmSecretKey);

		// given
		String validToken = jwtBuilder.compact();

		//then
		assertThrowsExactly(
			SignatureException.class,
			() -> jwtTokenValidator.getClaimsOrNullIfInvalid(validToken));

	}


	@Test
	@DisplayName("[성공] - 토큰이 유효한 경우")
	void getClaimsOrNullIfInvalid() {


		jwtTokenValidator = new JwtTokenValidator(secretKey);
		String validToken = "validToken";
		Claims mockClaims = mock(Claims.class);
		when(mockClaims.getSubject()).thenReturn("1");
		when(mockClaims.get("role")).thenReturn("USER");

		try (MockedStatic<Jwts> mocked = mockStatic(Jwts.class)) {
			JwtParser parser = mock(JwtParser.class, RETURNS_DEEP_STUBS);
			when(parser.parseSignedClaims(anyString()).getPayload()).thenReturn(mockClaims);

			mocked.when(Jwts::parser).thenReturn(mock(JwtParserBuilder.class, RETURNS_DEEP_STUBS));
			mocked.when(() -> Jwts.parser().verifyWith(secretKey).build()).thenReturn(parser);

			Optional<Claims> result = jwtTokenValidator.getClaimsOrNullIfInvalid(validToken);

			assertTrue(result.isPresent());
			assertEquals("1", result.get().getSubject());
			assertEquals("USER", result.get().get("role"));
		}

	}

	@Test
	@DisplayName("[실패] - 토큰이 만료된 경우")
	void getClaimsOrNullIfInvalidExpiredToken() {


		jwtTokenValidator = new JwtTokenValidator(secretKey);
		String validToken = "validToken";

		Claims mockClaims = mock(Claims.class);
		when(mockClaims.getSubject()).thenReturn("1");
		when(mockClaims.get("role")).thenReturn("USER");

		try (MockedStatic<Jwts> mocked = mockStatic(Jwts.class)) {
			JwtParser parser = mock(JwtParser.class, RETURNS_DEEP_STUBS);
			when(parser.parseSignedClaims(anyString()).getPayload()).thenThrow(
				new ExpiredJwtException(null, null, null));

			mocked.when(Jwts::parser).thenReturn(mock(JwtParserBuilder.class, RETURNS_DEEP_STUBS));
			mocked.when(() -> Jwts.parser().verifyWith(secretKey).build()).thenReturn(parser);

			Optional<Claims> result = jwtTokenValidator.getClaimsOrNullIfInvalid(validToken);

			assertTrue(result.isEmpty());
		}
	}

}