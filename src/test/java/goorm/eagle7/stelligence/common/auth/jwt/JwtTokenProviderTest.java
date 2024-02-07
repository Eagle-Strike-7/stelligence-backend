package goorm.eagle7.stelligence.common.auth.jwt;

import static org.assertj.core.api.Assertions.*;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import goorm.eagle7.stelligence.domain.member.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

@SpringBootTest
@TestPropertySource(properties = "spring.config.location=classpath:/application.yml")
class JwtTokenProviderTest {

	@Autowired
	private JwtTokenProvider tokenProvider;
	@Autowired
	private SecretKey key;

	private final Long memberId = 1L;

	@Test
	@DisplayName("accessToken 생성 - member의 ID, role 확인")
	void createAccessToken() {

		// given

		// when
		String accessToken = tokenProvider.createAccessToken(memberId);

		// then
		Claims payload = Jwts.parser()
			.verifyWith(key)
			.build()
			.parseSignedClaims(accessToken)
			.getPayload();
		Long memberIdFromToken = Long.parseLong(payload
			.getSubject());
		String role = (String)payload.get("role");
		Role roleFromToken = Role.valueOf(role);
		assertThat(memberIdFromToken).isEqualTo(memberId);
		assertThat(roleFromToken).isEqualTo(Role.USER);

	}

	@Test
	@DisplayName("refreshToken 생성 - member의 ID O role X ")
	void createRefreshToken() {

		// given

		// when
		String refreshToken = tokenProvider.createRefreshToken(memberId);

		// then
		Claims payload = Jwts.parser()
			.verifyWith(key)
			.build()
			.parseSignedClaims(refreshToken)
			.getPayload();
		Long memberIdFromToken = Long.parseLong(payload
			.getSubject());
		assertThat(memberIdFromToken).isEqualTo(memberId);
		assertThat(payload.get("role", String.class)).isNull();
	}

	@Test // TODO lambda로 변경 시 통과 불가 - 이유 확인 필요
	@DisplayName("refreshToken 만료시키기 - ExpiredJwtException 발생")
	void expireRefreshToken() {
		// given

		// when
		String refreshToken = tokenProvider.expireRefreshToken(memberId);

		// then
		assertThatThrownBy(
			() -> Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(refreshToken)
				.getPayload()
		).isInstanceOf(ExpiredJwtException.class);
	}
}