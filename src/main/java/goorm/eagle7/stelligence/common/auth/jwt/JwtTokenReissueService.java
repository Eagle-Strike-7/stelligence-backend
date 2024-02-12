package goorm.eagle7.stelligence.common.auth.jwt;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.common.login.CookieType;
import goorm.eagle7.stelligence.common.login.CookieUtils;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class JwtTokenReissueService {

	private final JwtTokenProvider jwtTokenProvider;
	private final JwtTokenService jwtTokenService;
	private final MemberRepository memberRepository;
	private final CookieUtils cookieUtils;

	/**
	 *
	 * accessToken 만료인 경우, RefreshToken의 유효성을 검증하고 재발급한다.
	 * 1. 쿠키의 refreshToken과 DB의 RefreshToken의 동등성 확인.
	 *  - 다르면 로그인 필요
	 * 2. refreshToken 만료 확인
	 *  - 만료면 로그인 필요
	 *  - 만료가 아니면 accessToken 재발급, 보안상 이유로 refreshToken도 재발급 및 저장
	 * @param refreshToken refreshToken
	 * @return accessToken
	 */
	public String reissueAccessToken(String refreshToken) {

		Claims refreshClaims = jwtTokenService.validateAndGetClaims(refreshToken);
		if(refreshClaims == null) {
			throw new BaseException("로그인이 필요합니다.");
		}
		Long memberId = jwtTokenService.getMemberId(refreshClaims);
		Member member = memberRepository.findById(memberId).orElseThrow(
			() -> new BaseException("로그인이 필요합니다.")
		);
		String refreshTokenFromDB = member.getRefreshToken();

		// DB에 저장된 refreshToken과 일치하지 않거나 refreshToken이 만료되었다면 로그인 필요
		if (validateIsEqualRefreshTokenToDB(refreshToken, refreshTokenFromDB) ||
			!jwtTokenService.validateToken(refreshToken)) {
			throw new BaseException("로그인이 필요합니다.");
		}

		// refreshToken 만료 X -> accessToken 재발급, 보안상 이유로 refreshToken 재발급 및 저장
		return reissueTokensAndSaveOnCookieAndDb(member);

	}

	/**
	 * refreshToken이 DB에 저장된 refreshToken과 일치하지 않는지 확인
	 * @param refreshToken 쿠키의 refreshToken
	 * @param refreshTokenFromDB DB에 저장된 refreshToken
	 * @return 일치하지 않으면 false // TODO validator로 옮기기.
	 */
	private static boolean validateIsEqualRefreshTokenToDB(String refreshToken, String refreshTokenFromDB) {
		if (!refreshToken.equals(refreshTokenFromDB)) {
			log.debug("refreshToken이 DB에 저장된 refreshToken과 일치하지 않습니다.");
			return false;
		}
		return true;
	}

	/**
	 * <h2>refreshToken 재발급</h2>
	 * <p>- accessToken, refreshToken 재발급</p>
	 * <p>- 쿠키에 저장</p>
	 * <p>- DB에 refreshToken 저장</p>
	 * @param member 회원
	 * @return accessToken
	 */
	private String reissueTokensAndSaveOnCookieAndDb(Member member) {
		Long memberId = member.getId();

		// Token 재발급
		String accessToken = jwtTokenProvider.createAccessToken(memberId);
		String newRefreshToken = jwtTokenProvider.createRefreshToken(memberId);

		// 쿠키에 저장
		cookieUtils.addCookieBy(CookieType.ACCESS_TOKEN, accessToken);
		cookieUtils.addCookieBy(CookieType.REFRESH_TOKEN, newRefreshToken);

		// DB에 저장
		member.updateRefreshToken(newRefreshToken);

		return accessToken;
	}

}
