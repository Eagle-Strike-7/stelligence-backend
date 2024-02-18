package goorm.eagle7.stelligence.common.dev;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.common.auth.jwt.JwtTokenProvider;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
import goorm.eagle7.stelligence.common.dev.dto.DevLoginRequest;
import goorm.eagle7.stelligence.common.dev.dto.DevLoginTokensWithIdAndRoleResponse;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DevLoginService {

	private final DevSignUpService devSignUpService;
	private final JwtTokenProvider jwtTokenProvider;
	private final MemberRepository memberRepository;

	@Transactional
	public DevLoginTokensWithIdAndRoleResponse devLogin(DevLoginRequest devLoginRequest) {

		// nickname으로 회원 조회 후 없으면 회원 가입 -> member 받아 오기
		// nickname 중복이면 로그인
		String nickname = devLoginRequest.getNickname();

		// nickname이 Null이어도 회원 가입 처리
		Member member =
			// 회원 확인
			memberRepository.existsByNickname(nickname)
				// 활성 회원이면(닉네임 기준) 로그인
				? memberRepository.findByNicknameAndActiveTrue(nickname).orElseGet(
					// 활성 회원의 닉네임이 아니면 회원 가입
					() -> devSignUpService
						.devSignUp(devLoginRequest)
				)
				:
				// 현재 회원이 아니면 회원 가입
				devSignUpService.devSignUp(devLoginRequest);

		// token 생성 후 저장, 쿠키 저장
		return generateAndSaveTokens(member);

	}

	/**
	 * 토큰 생성 후 저장
	 * @param member 회원
	 * @return accessToken, refreshToken
	 */
	private DevLoginTokensWithIdAndRoleResponse generateAndSaveTokens(Member member) {

		// Token 생성
		String accessToken = jwtTokenProvider.createAccessToken(member.getId());
		String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

		// refresh token 저장
		member.updateRefreshToken(refreshToken);

		return
			DevLoginTokensWithIdAndRoleResponse.of(
				accessToken,
				refreshToken,
				member.getId(),
				member.getRole()
			);
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
