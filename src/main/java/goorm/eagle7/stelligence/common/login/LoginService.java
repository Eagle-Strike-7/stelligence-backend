package goorm.eagle7.stelligence.common.login;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import goorm.eagle7.stelligence.common.auth.jwt.JwtTokenProvider;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
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
		String nickname = StringUtils.hasText(devLoginRequest.getNickname())
			? devLoginRequest.getNickname()
			: "은하";

		Member member = memberRepository.findByNicknameAndActiveTrue(nickname)
			.orElseGet(() -> signUpService.signUp(nickname));

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

	/**
	 * <h2>로그아웃</h2>
	 * <p>- 리프레시 토큰, 쿠키, ThreadLocal 삭제</p>
	 * @param memberInfo 회원 정보
	 */
	public void devLogout(MemberInfo memberInfo) {

		// 로그인 상태인 경우, refreshToken 삭제
		if (memberInfo != null) {
			memberRepository.findById(memberInfo.getId())
				.ifPresent(member -> member.updateRefreshToken(null));
		}

	}

}
