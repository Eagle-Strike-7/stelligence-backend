package goorm.eagle7.stelligence.common.login;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.common.auth.jwt.JwtTokenProvider;
import goorm.eagle7.stelligence.common.login.dto.LoginOAuth2Request;
import goorm.eagle7.stelligence.common.login.dto.LoginTokensWithIdAndRoleResponse;
import goorm.eagle7.stelligence.common.util.CookieType;
import goorm.eagle7.stelligence.common.util.CookieUtils;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LoginService {

	private final MemberRepository memberRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final SignUpService signUpService;
	private final CookieUtils cookieutils;

	@Transactional
	public Member login(HttpServletResponse response, LoginOAuth2Request loginOAuth2Request) {

		String socialId = loginOAuth2Request.getSocialId();

		// TODO socialId, socialType으로 회원 조회 후 없으면 회원 가입 -> member 받아 오기
		// socialId로 회원 조회 후 없으면 회원 가입 -> member 받아 오기
		Member member = memberRepository.findBySocialId(socialId)
			.orElseGet(() -> signUpService.signUp(loginOAuth2Request));

		// token 생성 후 refreshToken DB에 저장
		LoginTokensWithIdAndRoleResponse loginTokensWithIdAndRoleResponse = generateAndSaveTokens(member);

		// 발행된 토큰을 사용자의 브라우저 쿠키에 저장
		addTokensOnCookies(loginTokensWithIdAndRoleResponse);

		return member;
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
	protected LoginTokensWithIdAndRoleResponse generateAndSaveTokens(Member member) {

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

	@Transactional
	public void logout(Long memberId) {

		// db에서 refreshToken 삭제
		memberRepository.findById(memberId)
			.ifPresent(Member::expireRefreshToken);

	}

}
