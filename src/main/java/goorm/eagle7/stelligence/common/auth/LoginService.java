package goorm.eagle7.stelligence.common.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.common.auth.dto.LoginRequest;
import goorm.eagle7.stelligence.common.auth.dto.LoginTokensResponse;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional // TODO Transactional 걸어야 하는지 확인 필요,
@RequiredArgsConstructor
public class LoginService {

	private final MemberRepository memberRepository;
	private final JwtProvider jwtProvider;

	public LoginTokensResponse login(LoginRequest loginRequest) {

		// member 저장
		Member member = memberRepository.findByNickname(loginRequest.getNickname())
			.orElseGet(
				// member가 없다면 생성해 저장 후 반환
				() -> memberRepository.save(
					new Member("영민", loginRequest.getNickname(), "sbslc2000@stelligence.com", "star.com", "",
						"eeunzzi")));

		// Token 생성
		String accessToken = jwtProvider.createAccessToken(member.getId());
		String refreshToken = jwtProvider.createRefreshToken(member.getId());

		// TODO refresh token 저장
		member.updateRefreshToken(refreshToken);

		return
			LoginTokensResponse.of(
				accessToken,
				refreshToken
			);

	}

}
