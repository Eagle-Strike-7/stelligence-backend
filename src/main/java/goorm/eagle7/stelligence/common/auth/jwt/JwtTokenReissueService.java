package goorm.eagle7.stelligence.common.auth.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.common.login.CookieUtils;
import goorm.eagle7.stelligence.domain.member.MemberService;
import goorm.eagle7.stelligence.domain.member.model.Member;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * accessToken 만료인 경우, 이용하는 service
 *  - refreshToken의 유효성을 검증하고 재발급한다.
 * 1. refreshToken 만료 확인 -> 만료면 실패
 * 2. 쿠키의 refreshToken과 DB의 refreshToken의 동등성 확인. -> 다르면 실패
 * 3. 만료가 아니면 accessToken 재발급, 보안상 이유로 refreshToken도 재발급 및 저장
 *  -> 성공 시 accessToken 재발급
 *  -> 실패 시 로그인 필요, throw BadJwtException
 *  TODO refreshToken 기한 확인해서 Update할지 정하는 게 좋은지
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class JwtTokenReissueService {

	@Value("${http.cookie.accessToken.maxAge}")
	private String accessCookieMaxAge;
	@Value("${http.cookie.refreshToken.maxAge}")
	private String refreshCookieMaxAge;

	private final JwtTokenProvider jwtTokenProvider;
	private final JwtTokenService jwtTokenService;
	private final MemberService memberService;
	private static final String ERROR_MESSAGE = "유효하지 않은 사용자입니다.";


	/**
	 * - accessToken 재발급, 불가 시 throw BadJwtException
	 *
	 * - refresh 토큰 만료 확인
	 * 		   -> 유효하다면
	 * 		 	   -> DB와 동일한 refreshToken인지 확인
	 * 		       -> accessToken 재발급, 보안상 이유로 refreshToken 재발급 및 저장
	 * 		   -> 만료라면
	 * 		      throw BadJwtException
	 * @param response response cookie에 token 저장
	 * @param refreshToken refreshToken
	 * @param accessTokenName 쿠키의 accessTokenName
	 * @param refreshTokenName 쿠키의 refreshTokenName
	 * @return accessToken
	 */
	public String reissueAccessToken(HttpServletResponse response, String refreshToken,
		String accessTokenName,
		String refreshTokenName) {

		// refreshToken 만료 확인, 만료라면 throw BadJwtException
		jwtTokenService.validateTokenOrThrows(refreshToken);

		// memberId 추출
		Long memberId = jwtTokenService.getMemberId(refreshToken);

		// DB에서 refreshToken 가져와 문자열 비교(동등성 확인), 다르면 throw BadJwtException
		Member member = memberService.findMemberById(memberId);
		String refreshTokenFromDB = member.getRefreshToken();
		validateIsEqualRefreshTokenToDB(refreshToken, refreshTokenFromDB);

		// accessToken 재발급 및 쿠키에 추가 후 accessToken 반환
		return reissueTokensAndSaveOnCookieAndDb(response, accessTokenName, refreshTokenName, member);

	}

	/**
	 * refreshToken이 DB에 저장된 refreshToken과 일치하지 않는지 확인
	 * @param refreshToken 쿠키의 refreshToken
	 * @param refreshTokenFromDB DB에 저장된 refreshToken
	 * @return 일치하지 않으면 false
	 * @throws BadJwtException refreshToken이 DB에 저장된 refreshToken과 일치하지 않으면 throw
	 */
	private boolean validateIsEqualRefreshTokenToDB(String refreshToken, String refreshTokenFromDB) {
		if (!refreshToken.equals(refreshTokenFromDB)) {
			log.debug("refreshToken이 DB에 저장된 refreshToken과 일치하지 않습니다.");
			throw new BadJwtException(ERROR_MESSAGE);
		}
		return true;
	}

	/**
	 * accessToken, refreshToken 재발급 후 쿠키와 DB에 저장
	 * @param response response
	 * @param accessTokenName 쿠키의 accessTokenName
	 * @param refreshTokenName 쿠키의 refreshTokenName
	 * @param member 회원
	 * @return accessToken 재발급
	 */
	private String reissueTokensAndSaveOnCookieAndDb(HttpServletResponse response, String accessTokenName,
		String refreshTokenName, Member member) {
		Long memberId = member.getId();

		// Token 재발급
		String newAccessToken = jwtTokenProvider.createAccessToken(memberId);
		String newRefreshToken = jwtTokenProvider.createRefreshToken(memberId);

		// 쿠키에 저장
		CookieUtils.addCookie(response, accessTokenName, newAccessToken, accessCookieMaxAge);
		CookieUtils.addCookie(response, refreshTokenName, newRefreshToken, refreshCookieMaxAge);

		// DB에 저장
		member.updateRefreshToken(newRefreshToken);

		return newAccessToken;
	}

}
