package goorm.eagle7.stelligence.common.auth.jwt;

import static org.assertj.core.api.Assertions.*;

import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import goorm.eagle7.stelligence.domain.member.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

/**
 * <h2>token 생성 확인</h2>
 * <p>- yml 설정값이 많아 bootTest로 진행</p>
 */
@Slf4j
@SpringBootTest
class JwtTokenProviderTest {

	@Autowired
	private JwtTokenProvider tokenProvider;
	@Autowired
	private SecretKey key;

	private SecretKey testKey;
	private final Long memberId = 1L;

	@BeforeEach
	void setUp() {

		// 테스트용 검증 key 생성
		byte[] decodedKey = Base64.getDecoder().decode("jwtSecretsafasfjans345344kfnaskjfowefoiwnefoinwofn03844h8204h20hf820fdnfnf934534+345H4");
		testKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA512");

	}

	@Test
	@DisplayName("[성공] accessToken 생성 - member의 ID O role O")
	void createAccessToken() {

		// given

		// when
		String accessToken = tokenProvider.createAccessToken(memberId);
		log.info("accessToken: {}", accessToken);

		// then
		// 토큰 검증, 검증이 필요해 파싱, 검증을 통해 생성된 내용 확인
		Claims payload = Jwts.parser()
			.verifyWith(key)
			.build()
			.parseSignedClaims(accessToken)
			.getPayload();
		Long actualMemberId = Long.parseLong(payload
			.getSubject());
		Role role = Role.valueOf(payload.get("role", String.class));

		// 실제 생성됐는지, member의 ID가 일치하는지, role이 존재하는지 확인
		assertThat(accessToken).isNotNull();
		assertThat(actualMemberId).isEqualTo(memberId);
		assertThat(role).isNotNull();

	}

	@Test
	@DisplayName("[성공] refreshToken 생성 - member의 ID O role X ")
	void createRefreshToken() {

		// given

		// when
		String refreshToken = tokenProvider.createRefreshToken(memberId);
		log.info("refreshToken: {}", refreshToken);

		// then
		// 토큰 검증, 검증이 필요해 파싱, 검증을 통해 생성된 내용 확인
		Claims payload = Jwts.parser()
			.verifyWith(key)
			.build()
			.parseSignedClaims(refreshToken)
			.getPayload();
		Long actualMemberId = Long.parseLong(payload
			.getSubject());

		// 실제 생성됐는지, member의 ID가 일치하는지, role이 존재하지 않는지 확인
		assertThat(refreshToken).isNotNull();
		assertThat(actualMemberId).isEqualTo(memberId);
		assertThat(payload.get("role", String.class)).isNull();

	}

	@Test
	@DisplayName("[성공] refreshToken 만료시키기 - ExpiredJwtException 발생")
	void expireRefreshToken() {

		// given

		// when
		String expiredRefreshToken = tokenProvider.expireRefreshToken(memberId);
		log.info("expiredRefreshToken: {}", expiredRefreshToken);

		// then
		// 만료된 토큰이므로 ExpiredJwtException 발생
		assertThatThrownBy(
			() -> Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(expiredRefreshToken)
		).isInstanceOf(ExpiredJwtException.class);
		// TODO lambda로 변경 시 통과 불가 - 이유 확인 필요

	}

	/**
	 * <h2>[확인] issuedAt 외 설정하지 않는 경우 설정 정보 확인</h2>
	 * <p>- issuedAt은 설정해야 parse 가능.</p>
	 * <p> 결과: header - alg는 key로 자동 생성됨.</p>
	 */
	@Test
	@DisplayName("[확인] issuedAt 외 설정하지 않는 경우, header - alg는 key로 자동 생성됨.")
	void autoSetHeader() {

		// given
		String noContentToken = Jwts.builder()
			.issuedAt(new Date(System.currentTimeMillis()))
			.signWith(testKey)
			.compact();
		log.info("noHeaderToken: {}", noContentToken);

		// when
		Jws<Claims> claimsJws = Jwts.parser()
			.verifyWith(testKey)
			.build()
			.parseSignedClaims(noContentToken);
		JwsHeader header =
			claimsJws.getHeader();
		Date expiration = claimsJws
			.getPayload()
			.getExpiration();

		// then - header의 alg는 key로 자동 생성, 그 외 정보는 null
		assertThat(header.get("alg")).isEqualTo("HS512");
		assertThat(header.get("typ")).isNull();
		assertThat(expiration).isNull();

	}

	/**
	 * <h2>[확인] header의 alg와 key의 내용이 다른 경우 토큰 내용 확인</h2>
	 * <p>- 토큰 header의 alg이 key와 다른 경우 토큰 내용 확인</p>
	 * <p> 결과: header의 alg이 key와 다른 경우, key로 설정됨</p>
	 */
	@Test
	@DisplayName("[확인] header의 alg이 key와 다른 경우, key로 설정됨")
	void compareTypBetweenHeaderAndKey() {

		// given
		String differentHeaderToken = Jwts.builder()
			.header()
			.add("typ", "일치하지 않는 타입")
			.add("alg", "일치하지 않는 알고리즘")
			.and()
			.issuedAt(new Date(System.currentTimeMillis()))
			.signWith(testKey)
			.compact();
		log.info("differentHeaderToken: {}", differentHeaderToken);

		// when
		JwsHeader header = Jwts.parser()
			.verifyWith(testKey)
			.build()
			.parseSignedClaims(differentHeaderToken)
			.getHeader();

		// then
		assertThat(header.get("alg")).isEqualTo("HS512");
		assertThat(header.get("typ")).isEqualTo("일치하지 않는 타입");

	}

}