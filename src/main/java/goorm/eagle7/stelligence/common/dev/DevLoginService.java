package goorm.eagle7.stelligence.common.dev;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.common.auth.jwt.JwtTokenProvider;
import goorm.eagle7.stelligence.common.login.dto.LoginTokensWithIdAndRoleResponse;
import goorm.eagle7.stelligence.common.util.CookieType;
import goorm.eagle7.stelligence.common.util.CookieUtils;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DevLoginService {

	private final CookieUtils cookieutils;
	private final DevSignUpService devSignUpService;
	private final JwtTokenProvider jwtTokenProvider;
	private final MemberRepository memberRepository;


	@Transactional
	public LoginTokensWithIdAndRoleResponse devLogin( DevLoginRequest devLoginRequest) {

		// nickname으로 회원 조회 후 없으면 회원 가입 -> member 받아 오기
		Member member = memberRepository.findByNicknameAndActiveTrue(devLoginRequest.getNickname())
			.orElseGet(() -> devSignUpService.devSignUp(devLoginRequest));
		// nickname 중복이면 로그인

		// token 생성 후 refreshToken DB에 저장
		LoginTokensWithIdAndRoleResponse loginTokensWithIdAndRoleResponse = generateAndSaveTokens(member);

		// 발행된 토큰을 사용자의 브라우저 쿠키에 저장
		addTokensOnCookies(loginTokensWithIdAndRoleResponse);

		return loginTokensWithIdAndRoleResponse;
	}

	private void addTokensOnCookies(LoginTokensWithIdAndRoleResponse loginTokensWithIdAndRoleResponse) {

		// 토큰 추출
		String accessToken = loginTokensWithIdAndRoleResponse.getAccessToken();
		String refreshToken = loginTokensWithIdAndRoleResponse.getRefreshToken();

		// 발행된 토큰을 사용자의 브라우저 쿠키에 저장
		cookieutils.addCookieBy(CookieType.ACCESS_TOKEN, accessToken);
		cookieutils.addCookieBy(CookieType.REFRESH_TOKEN, refreshToken);
	}

	/**
	 * 토큰 생성 후 저장
	 * @param member 회원
	 * @return accessToken, refreshToken
	 */
	private LoginTokensWithIdAndRoleResponse generateAndSaveTokens(Member member) {

		// Token 생성
		String accessToken = jwtTokenProvider.createAccessToken(member.getId());
		String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

		// refresh token 저장
		member.updateRefreshToken(refreshToken);

		return
			LoginTokensWithIdAndRoleResponse.of(
				accessToken,
				refreshToken,
				member.getId(),
				member.getRole()
			);
	}


}
