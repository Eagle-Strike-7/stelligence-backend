package goorm.eagle7.stelligence.common.auth;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import goorm.eagle7.stelligence.common.auth.jwt.JwtTokenProvider;
import goorm.eagle7.stelligence.common.auth.jwt.JwtTokenService;
import io.jsonwebtoken.Claims;

@SpringBootTest
class JwtTokenServiceTest {

	@Autowired
	private JwtTokenService jwtTokenService;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	/**
	 * jwtService의 getMemberId를 테스트한다.
	 * accessToken을 받으면, 해당 accessToken에 실린 memberId를 조회.
	 * 현재는 accessToken을 provider로 생성하고, 해당 accessToken을 이용해 getMemberId 메서드로  memberId를 가져온다.
	 * TODO jwtProvider 의존 없이 test할 수 있도록.
	 */
	@Test
	void getMemberId() {

		String accessToken = jwtTokenProvider.createAccessToken(1L);
		Claims claims = jwtTokenService.validateAndGetClaims(accessToken);
		Long memberId = jwtTokenService.getMemberInfo(claims).getId();
		Assertions.assertThat(memberId).isEqualTo(1L);

	}
}