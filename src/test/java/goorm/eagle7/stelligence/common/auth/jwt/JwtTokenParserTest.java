package goorm.eagle7.stelligence.common.auth.jwt;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.domain.member.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

		byte[] decodedKey = Base64.getDecoder()
			.decode(
				"jwtSecretjofoianeofnwong94njndslnlkdsdflsnkdlfsdofhasdhf02hf9hf4fhla48fa4a4fsjldfnsiodfnsd8n34n534n94384");
		// 생성된 바이트 배열로부터 SecretKey 생성
		secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");

		token = Jwts.builder()
			.subject("100")
			.claim("hi", "USER")
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
	@DisplayName("[예외] sub이 없는 경우, null 반환 - getSubject")
	void getSubjectNull() {

		// given
		String noSubToken = Jwts.builder()
			.issuedAt(new Date())
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
	@DisplayName("[예외] claims가 null인 경우, NP - getSubject")
	void getSubjectNullClaims() {

		// given - null, when, then
		assertThatThrownBy(() ->
			jwtTokenParser.getSubject(null))
			.isInstanceOf(NullPointerException.class);

	}

	@Test
	@DisplayName("[성공] 토큰에서 사용자 정의 claims 추출")
	void getRole() {

		// given
		Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
		String claimKey = "hi";

		// when
		Role actualRole = jwtTokenParser.getRole(claims, claimKey);

		// then
		assertThat(actualRole)
			.isEqualTo(Role.valueOf(
				claims.get(claimKey, String.class)));
		log.info("actualRole: {}", actualRole);
		log.info("claims.get(claimKey, String.class): {}", claims.get(claimKey, String.class));
		log.info("claims: {}", claims);

	}

	@Test
	@DisplayName("[예외] 해당하는 사용자 정의 claims가 없을 경우, null - getRole")
	void getRoleNull() {

		// given
		Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
		String claimKey = "no";

		// when, then
		assertThatThrownBy(() ->
			jwtTokenParser.getRole(claims, claimKey))
			.isInstanceOf(NullPointerException.class);

	}

	@Test
	@DisplayName("[성공] 사용자 정의 claims가 여러 개인 경우 - getRole")
	void getRoleMultiple() {

		// given
		String multipleClaimsToken = Jwts.builder()
			.subject("100")
			.claim("안녕", "USER")
			.claim("hi", "ADMIN")
			.signWith(secretKey)
			.compact();
		Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(multipleClaimsToken).getPayload();
		String claimKey1 = "hi";
		String claimKey2 = "안녕";

		// when
		Role actualRole1 = jwtTokenParser.getRole(claims, claimKey1);
		Role actualRole2 = jwtTokenParser.getRole(claims, claimKey2);

		// then
		assertThat(actualRole1)
			.isEqualTo(Role.valueOf(
				claims.get(claimKey1, String.class)));
		assertThat(actualRole2)
			.isEqualTo(Role.valueOf(
				claims.get(claimKey2, String.class)));
		log.info("claims.get(claimKey1, String.class): {}", claims.get(claimKey1, String.class));
		log.info("claims: {}", claims);

	}

	@Test
	void extractSubFromExpiredToken() {

	}
}