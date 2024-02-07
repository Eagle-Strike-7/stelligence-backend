package goorm.eagle7.stelligence.common.auth.jwt;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.common.util.CookieType;
import goorm.eagle7.stelligence.common.util.CookieUtils;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <h2>accessToken 만료인 경우, 이용하는 service</h2>
 * <p>- 성공 시 accessToken 재발급, 실패 시 throws, 재로그인</p>
 * <p>- refreshToken의 유효성을 검증하고 재발급한다.</p>
 * <p>1. refreshToken 만료 확인 -> 만료면 실패</p>
 * <p>2. 쿠키의 refreshToken과 DB의 refreshToken의 동등성 확인. -> 다르면 실패</p>
 * <p>3. 만료가 아니면 accessToken 재발급, 보안상 이유로 refreshToken도 재발급 및 저장</p>
 *  TODO refreshToken 기한 확인해서 Update할지 정하는 게 좋은지
 *  TODO Transactional 작게 적용하는 법
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenReissueService {

	private final CookieUtils cookieUtils;
	private final JwtTokenService jwtTokenService;
	private final JwtTokenProvider jwtTokenProvider;
	private final MemberRepository memberRepository;
	private static final String ERROR_MESSAGE = "유효하지 않은 사용자입니다.";

	/**
	 * <h2>refreshToken 검증해 accessToken 재발급</h2>
	 * <p>- refreshToken이 만료되었는지 확인</p>
	 * <p>- refreshToken이 DB에 저장된 refreshToken과 일치하지 않는지 확인</p>
	 * <p>- accessToken, refreshToken 재발급, 불가 시 throw BadJwtException</p>
	 * @param refreshToken 검증할 refreshToken
	 * @return accessToken 재발급한 accessToken
	 * @throws UsernameNotFoundException memberId에 해당하는 회원이 없습니다.
	 * @throws BadJwtException refreshToken이 만료되었습니다.
	 */
	@Transactional
	public String reissueAccessToken(String refreshToken) {

		log.debug("refreshToken 재발급 시작");

		// refreshToken 검증 및 member 조회
		Long memberId = jwtTokenService.getMemberId(refreshToken);
		Member member = memberRepository
			.findByIdAndActiveTrue(memberId)
			.orElseThrow(() -> {
				log.debug("memberId에 해당하는 회원이 없습니다.");
				return new UsernameNotFoundException(ERROR_MESSAGE);
			});

		// DB에서 refreshToken 가져와 문자열 비교(동등성 확인), 다르면 throw ex (401)
		validateTokenEquality(refreshToken, member.getRefreshToken());

		// accessToken 재발급 및 쿠키에 추가 후 accessToken 반환
		return reissueTokens(member);

	}

	/**
	 * <h2>refreshToken이 DB에 저장된 것과 동일한지 확인</h2>
	 * @param refreshToken 쿠키의 refreshToken
	 * @param refreshTokenFromDB DB에 저장된 refreshToken
	 * @throws BadJwtException refreshToken이 DB에 저장된 refreshToken과 일치하지 않으면 throw
	 */
	private void validateTokenEquality(String refreshToken, String refreshTokenFromDB) {

		if (!refreshToken.equals(refreshTokenFromDB)) {
			log.debug("refreshToken이 DB에 저장된 refreshToken과 일치하지 않습니다.");
			throw new BadJwtException(ERROR_MESSAGE);
		}

	}

	/**
	 * <h2>accessToken, refreshToken 재발급</h2>
	 * <p>- accessToken, refreshToken 재발급 후 쿠키와 DB에 저장</p>
	 * @param member token으로 검증한 회원
	 * @return accessToken 재발급한 accessToken
	 */
	private String reissueTokens(Member member) {

		log.debug("accessToken, refreshToken 재발급 진행");

		Long memberId = member.getId();

		// Token 재발급
		String newAccessToken = jwtTokenProvider.createAccessToken(memberId);
		String newRefreshToken = jwtTokenProvider.createRefreshToken(memberId);

		// 쿠키에 저장
		cookieUtils.addCookieBy(CookieType.ACCESS_TOKEN, newAccessToken);
		cookieUtils.addCookieBy(CookieType.REFRESH_TOKEN, newRefreshToken);

		// DB에 리프레시 토큰 업데이트
		member.updateRefreshToken(newRefreshToken);

		return newAccessToken;

	}

}
