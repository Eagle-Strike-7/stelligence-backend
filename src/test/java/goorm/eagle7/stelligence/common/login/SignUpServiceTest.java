package goorm.eagle7.stelligence.common.login;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import goorm.eagle7.stelligence.common.login.dto.LoginOAuth2Request;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.member.model.SocialType;
import lombok.extern.slf4j.Slf4j;

// @ExtendWith(MockitoExtension.class)
@Slf4j
@SpringBootTest
class SignUpServiceTest {

	// @Mock
	@Autowired
	private MemberRepository memberRepository;

	// @InjectMocks
	@Autowired
	private SignUpService signUpService;

	@BeforeEach
	void setUp() {
		Member member = signUpService.oauth2SignUp(
			LoginOAuth2Request.of("name", "baseNickname", "email", "imageUrl", "socialId123",
				SocialType.KAKAO));
		memberRepository.save(member);
	}

	@AfterEach
	void tearDown() {
		memberRepository.deleteAll();
	}

	@Test
	@DisplayName("[성공] 닉네임 중복 X, OAuth2 회원 가입 테스트 - oauth2SignUp")
	void testOauth2SignUp() {

		// given
		LoginOAuth2Request loginOAuth2Request = LoginOAuth2Request.of("name", "nickname", "email", "imageUrl",
			"socialId", SocialType.KAKAO
		);

		// when
		Member member = signUpService.oauth2SignUp(loginOAuth2Request);

		// then
		// member의 원래 Nickname으로 잘 저장됐는지 확인
		assertThat(member).isNotNull();
		assertThat(member.getNickname()).isEqualTo("nickname");
		assertThat(member.getEmail()).isEqualTo("email");

	}

	@Test
	@DisplayName("[성공] 닉네임 중복 O, OAuth2 회원 가입 테스트 - oauth2SignUp")
	void testIsNicknameDuplicate() {

		// given
		LoginOAuth2Request loginOAuth2Request = LoginOAuth2Request.of("name", "baseNickname", "email", "imageUrl",
			"socialId", SocialType.KAKAO
		);

		// when
		Member member = signUpService.oauth2SignUp(loginOAuth2Request);

		// then
		// 새로운 닉네임으로 잘 저장되었는지 확인
		assertThat(member).isNotNull();
		assertThat(member.getNickname()).isNotEqualTo("baseNickname");
		assertThat(member.getEmail()).isEqualTo("email");

		log.info("member: {}", member);
		log.info("member.getNickname(): {}", member.getNickname());

	}

}