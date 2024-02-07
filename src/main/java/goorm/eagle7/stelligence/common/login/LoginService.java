package goorm.eagle7.stelligence.common.login;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.common.auth.jwt.JwtTokenProvider;
import goorm.eagle7.stelligence.common.login.dto.LoginOAuth2Request;
import goorm.eagle7.stelligence.common.util.CookieType;
import goorm.eagle7.stelligence.common.util.CookieUtils;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LoginService {

	private final CookieUtils cookieutils;
	private final SignUpService signUpService;
	private final MemberRepository memberRepository;
	private final JwtTokenProvider jwtTokenProvider;

	/**
	 * <h2>OAuth2 로그인</h2>
	 * <p>- 회원 가입 - socialId, socialType으로 회원 조회 후 없으면 회원 가입</p>
	 * <p>- 로그인 - token 생성 후 쿠키에 저장 및 refreshToken 반환</p>
	 * <p>- DB에 refreshToken 저장</p>
	 * @param loginOAuth2Request OAuth2 로그인 요청 정보
	 */
	@Transactional
	public void oAuth2Login(LoginOAuth2Request loginOAuth2Request) {

		String socialId = loginOAuth2Request.getSocialId();

		// socialId, socialType으로 회원 조회 후 없으면 회원 가입 -> member 받아 오기
		Member member = memberRepository.findBySocialTypeAndSocialIdAndActiveTrue(
				loginOAuth2Request.getSocialType().name(), socialId)
			.orElseGet(() -> signUpService.signUp(loginOAuth2Request));

		// 로그인 - token 생성 후 쿠키에 저장 및 refreshToken 반환
		String refreshToken = loginAndGetRefreshToken(member);

		// refresh token 저장
		member.updateRefreshToken(refreshToken);

	}

	/**
	 * <h2>토큰 생성 후 저장</h2>
	 * <p>- 로그인 회원의 accessToken, refreshToken 생성</p>
	 * <p>- 발행된 토큰을 사용자의 브라우저 쿠키에 저장</p>
	 * @param member 로그인 회원
	 * @return DB에 저장할 refreshToken
	 */
	protected String loginAndGetRefreshToken(Member member) {

		// Token 생성
		String accessToken = jwtTokenProvider.createAccessToken(member.getId());
		String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

		// 발행된 토큰을 사용자의 쿠키에 저장
		addTokensOnCookies(accessToken, refreshToken);

		return refreshToken;

	}

	/**
	 * <h2>토큰 쿠키 생성</h2>
	 * <p>- 발행된 토큰을 사용자의 브라우저 쿠키에 저장</p>
	 * @param accessToken accessToken 값
	 * @param refreshToken refreshToken 값
	 */
	private void addTokensOnCookies(String accessToken, String refreshToken) {

		cookieutils.addCookieBy(CookieType.ACCESS_TOKEN, accessToken);
		cookieutils.addCookieBy(CookieType.REFRESH_TOKEN, refreshToken);

	}

	/**
	 * <h2>로그아웃</h2>
	 * <p>- DB에서 refreshToken 삭제</p>
	 * @param memberId 로그아웃할 회원 id
	 */
	@Transactional
	public void logout(Long memberId) {

		memberRepository
			.findById(memberId)
			.ifPresent(Member::expireRefreshToken);

	}

}
