package goorm.eagle7.stelligence.common.auth.oauth.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.common.login.LoginService;
import goorm.eagle7.stelligence.common.util.CookieType;
import goorm.eagle7.stelligence.common.util.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LogoutCustomHandler implements LogoutHandler {

	private final LoginService loginService;
	private final CookieUtils cookieUtils;

	/**
	 * <h2>로그아웃 처리</h2>
	 * <p>- logout 시 refreshToken DB에서 삭제</p>
	 * <p>- 쿠키 삭제</p>
	 * @param request the HTTP request
	 * @param response the HTTP response
	 * @param authentication the current principal details
	 */
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

		// Authentication에서 사용자 정보 추출, name == memeberId
		// 로그인하지 않은 사용자가 로그아웃 요청 시, 아무것도 하지 않음
		if (authentication != null
			&& authentication.getPrincipal() instanceof UserDetails user) {

			Long memberId = Long.parseLong(user.getUsername());

			loginService.logout(memberId);

			// security 쿠키 삭제는 sameSite 설정 불가해 자체 삭제
			cookieUtils.deleteCookieBy(CookieType.ACCESS_TOKEN);
			cookieUtils.deleteCookieBy(CookieType.REFRESH_TOKEN);

		}

	}

}

