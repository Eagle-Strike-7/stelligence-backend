package goorm.eagle7.stelligence.common.auth.oauth.handler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.common.auth.oauth.CustomOAuth2User;
import goorm.eagle7.stelligence.common.login.LoginService;
import goorm.eagle7.stelligence.common.login.dto.LoginOAuth2Request;
import goorm.eagle7.stelligence.common.login.dto.LoginTokenInfo;
import goorm.eagle7.stelligence.common.util.CookieType;
import goorm.eagle7.stelligence.common.util.CookieUtils;
import goorm.eagle7.stelligence.common.util.ResponseTemplateUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Value("${spring.security.oauth2.redirect.uri}")
	private String redirectUrl;

	private final CookieUtils cookieutils;
	private final LoginService loginService;

	/**
	 * <h2>OAuth2 로그인 성공 시 호출되는 메서드</h2>
	 * <p>- 로그인 성공 시, 200 코드, 회원 가입 혹은 로그인</p>
	 * <p>- OAuth2User가 아닌 경우, InternalAuthenticationServiceException 발생</p>
	 * @param authentication 인증 객체, Service에서 반환한 CustomOAuth2User 저장되어 있음.
	 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) {

		if (!(authentication.getPrincipal() instanceof CustomOAuth2User customOAuth2User)) {
			// OAuth2User가 아닌 경우 에러, Spring Security의 예외 처리 체인을 따라 401 처리됨.
			throw new InternalAuthenticationServiceException("Unsupported user type");
		}

		// loginServie를 이용할 Dto 생성
		LoginOAuth2Request loginOAuth2Request = LoginOAuth2Request.of(
			customOAuth2User.getNickname(),
			customOAuth2User.getEmail(),
			customOAuth2User.getImageUrl(),
			customOAuth2User.getSocialId(),
			customOAuth2User.getSocialType()
		);

		// 로그인 서비스를 통해 토큰 발급
		LoginTokenInfo tokenInfo = loginService.oAuth2Login(loginOAuth2Request);

		// 토큰 쿠키 추가
		storeTokensInCookies(
			tokenInfo.getAccessToken(),
			tokenInfo.getRefreshToken());

		// 성공 응답 200 전송, redirectUrl로 리다이렉트
		try {
			response.sendRedirect(redirectUrl);
		} catch (IOException e) {
			// 리다이렉트 실패 시, 500 에러 응답
			ResponseTemplateUtils.toErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				ResponseTemplate.fail("Redirect failed"));
		}

	}

	/**
	 * <h2>토큰 쿠키 추가</h2>
	 * @param accessToken accessToken
	 * @param refreshToken refreshToken
	 */
	private void storeTokensInCookies(String accessToken, String refreshToken) {
		cookieutils.addCookieBy(CookieType.ACCESS_TOKEN, accessToken);
		cookieutils.addCookieBy(CookieType.REFRESH_TOKEN, refreshToken);
	}

}
