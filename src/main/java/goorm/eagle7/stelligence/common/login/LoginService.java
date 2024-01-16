package goorm.eagle7.stelligence.common.login;

import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.common.auth.jwt.JwtTokenProvider;
import goorm.eagle7.stelligence.common.login.dto.LoginRequest;
import goorm.eagle7.stelligence.common.login.dto.LoginTokensResponse;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {

	private final MemberRepository memberRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final Random random = new Random();

	public LoginTokensResponse login(LoginRequest loginRequest) {

		// nickname 중복 확인 후 중복이면 랜덤 생성
		String nickname = loginRequest.getNickname();
		nickname = generateUniqueNickname(nickname);

		// TODO OAuth2.0 테스트용 하드 코딩
		Member member1 = new Member("영민", nickname, "sbslc2000@stelligence.com", "star.com", "",
			"eeunzzi");

		// 해당 닉네임으로 저장
		Member member = memberRepository.save(
			member1);

		// Token 생성
		String accessToken = jwtTokenProvider.createAccessToken(member.getId());
		String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
		String socialTypeToken = jwtTokenProvider.createSocialTypeToken(member.getSocialType());

		// refresh token 저장
		member.updateRefreshToken(refreshToken);

		return
			LoginTokensResponse.of(
				accessToken,
				refreshToken,
				socialTypeToken
			);

	}

	// 닉네임 중복 확인 메서드 (데이터베이스 조회 로직은 구현에 따라 달라짐)
	private boolean isNicknameDuplicate(String nickname) {
		return memberRepository.findByNickname(nickname).isPresent();
	}

	// 닉네임 생성 메서드
	@Transactional
	public String generateUniqueNickname(String nickname) {
		while (isNicknameDuplicate(nickname)) {
			nickname = createNicknameWithRandomNumber(nickname);
		}
		return nickname;
	}

	// 5자리 랜덤 숫자를 추가하는 메서드
	private String createNicknameWithRandomNumber(String nickname) {

		int randomNumber = 10000 + random.nextInt(90000); // 10000 ~ 99999 범위의 숫자 생성
		return nickname + randomNumber;
	}

}
