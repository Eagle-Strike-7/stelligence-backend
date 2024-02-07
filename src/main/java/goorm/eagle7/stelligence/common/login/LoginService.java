package goorm.eagle7.stelligence.common.login;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.common.auth.jwt.JwtTokenProvider;
import goorm.eagle7.stelligence.common.login.dto.LoginOAuth2Request;
import goorm.eagle7.stelligence.common.login.dto.LoginTokenInfo;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LoginService {

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
	public LoginTokenInfo oAuth2Login(LoginOAuth2Request loginOAuth2Request) {

		// 회원 가입 - socialId, socialType으로 회원 조회 후 없으면 회원 가입
		Member member = findOrRegisterMember(loginOAuth2Request);

		// Token 생성
		String accessToken = jwtTokenProvider.createAccessToken(member.getId());
		String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

		return LoginTokenInfo.of(
			accessToken,refreshToken);

	}

	/**
	 * <h2>회원 가입 - socialId, socialType으로 회원 조회 후 없으면 회원 가입</h2>
	 * <p>- socialId, socialType으로 회원 조회 후 없으면 회원 가입</p>
	 * @param request OAuth2 로그인 요청 정보
	 * @return Member 로그인한 회원
	 */
	private Member findOrRegisterMember(LoginOAuth2Request request) {
		return memberRepository.findBySocialTypeAndSocialIdAndActiveTrue(request.getSocialType().name(), request.getSocialId())
			.orElseGet(() -> signUpService.oauth2SignUp(request));
	}

	/**
	 * <h2>로그아웃</h2>
	 * <p>- DB에서 refreshToken 삭제</p>
	 * @param memberId 로그아웃할 회원 id
	 */
	@Transactional
	public void logout(Long memberId) {

		memberRepository
			.findByIdAndActiveTrue(memberId)
			.ifPresent(Member::expireRefreshToken);

	}

}
