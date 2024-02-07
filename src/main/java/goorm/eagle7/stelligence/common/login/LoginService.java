package goorm.eagle7.stelligence.common.login;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.common.auth.jwt.JwtTokenProvider;
import goorm.eagle7.stelligence.common.login.dto.DevLoginRequest;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {

	private final MemberRepository memberRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final SignUpService signUpService;
	private final CookieUtils cookieUtils;

	public String login(DevLoginRequest devLoginRequest) {

		// nickname으로 회원 조회 후 없으면 회원 가입 -> member 받아 오기
		// nickname 중복이면 로그인
		Member member = memberRepository.findByNicknameAndActiveTrue(devLoginRequest.getNickname())
			.orElseGet(() -> signUpService.signUp(devLoginRequest.getNickname()));

		// token 생성 후 저장, 쿠키 저장
		return generateAndSaveTokens(member);
	}

	/**
	 * 토큰 생성 후 저장
	 * @param member 회원
	 * @return access 토큰 - for dev
	 */
	private String generateAndSaveTokens(Member member) {

		// Token 생성
		String accessToken = jwtTokenProvider.createAccessToken(member.getId());
		String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

		// refresh token 저장
		member.updateRefreshToken(refreshToken);

		// cookie에 access 토큰, refreshToken 저장
		cookieUtils.addCookieBy(CookieType.ACCESS_TOKEN, accessToken);
		cookieUtils.addCookieBy(CookieType.REFRESH_TOKEN, refreshToken);

		return accessToken;
	}

}
