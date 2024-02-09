package goorm.eagle7.stelligence.common.auth.jwt;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Base64;
import java.util.Optional;

import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@ExtendWith(MockitoExtension.class)
class JwtTokenParserTest {

	private SecretKeySpec secretKey;
	private String token;

	@Mock
	private JwtTokenValidator jwtTokenValidator;
	@InjectMocks
	private JwtTokenParser jwtTokenParser;

	@BeforeEach
	void setUp() {

		byte[] decodedKey = Base64.getDecoder().decode("jwtSecretjofoianeofnwong94njndslnlkdsdflsnkdlfsdofhasdhf02hf9hf4fhla48fa4a4fsjldfnsiodfnsd8n34n534n94384");
		// 생성된 바이트 배열로부터 SecretKey 생성
		secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");

		token = Jwts.builder()
			.subject("100")
			.claim("hi", "happyDay")
			.signWith(secretKey)
			.compact();

	}

	@Test
	@DisplayName("[성공] token에서 claims 추출 - getClaims")
	void getClaimsSuccess() {

		// given
		Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
		when(jwtTokenValidator.getClaimsOrNullIfInvalid(token))
			.thenReturn(Optional.of(claims));

		// when
		Optional<Claims> actualClaims = jwtTokenParser.getClaims(token);

		// then
		assertThat(actualClaims)
			.isNotEmpty()
			.containsSame(claims);

	}

	@Test
	@DisplayName("[처리] token이 유효하지 않은 경우, empty 반환 - getClaims")
	void getClaimsExpired() {

		// given
		when(jwtTokenValidator.getClaimsOrNullIfInvalid(token))
			.thenReturn(Optional.empty());

		// when
		Optional<Claims> actualClaims = jwtTokenParser.getClaims(token);

		// then
		assertThat(actualClaims)
			.isEmpty();

	}

	@Test
	@DisplayName("[성공] token에서 sub(memberId) 추출")
	void getSubject() {

		// given
		Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();

		// when
		String actualSubject = jwtTokenParser.getSubject(claims);

		// then
		assertThat(actualSubject)
			.isEqualTo(claims.getSubject());
	}

	@Test // TODO 처리 필요
	@DisplayName("[확인] sub이 없는 경우, null 반환 - getSubject")
	void getSubjectNull() {

		// given
		String noSubToken = Jwts.builder()
			.issuedAt(null)
			.signWith(secretKey)
			.compact();

		Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(noSubToken).getPayload();

		// when
		String actualSubject = jwtTokenParser.getSubject(claims);

		// then
		assertThat(actualSubject)
			.isNull();

	}

	@Test
	@DisplayName("[확인] claims가 null인 경우, NP - getSubject")
	void getSubjectNullClaims() {

		// given - null, when, then
		assertThatThrownBy(() ->
			jwtTokenParser.getSubject(null))
			.isInstanceOf(NullPointerException.class);

	}


	@Test
	void getRole() {
	}

	@Test
	void extractSubFromExpiredToken() {
	}
}