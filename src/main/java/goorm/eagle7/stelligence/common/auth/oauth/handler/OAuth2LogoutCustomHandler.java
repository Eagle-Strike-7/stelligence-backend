package goorm.eagle7.stelligence.common.auth.oauth.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
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
	 * 로그아웃 요청 시 호출되는 메서드 (/api/logout)
	 * 		- logout 시 refreshToken을 DB에서 삭제
	 * 		- 쿠키, Authentication 삭제는 Spring Security에서 처리
	 * @param request the HTTP request
	 * @param response the HTTP response
	 * @param authentication the current principal details
	 */
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

		// Authentication에서 사용자 정보 추출, name == memeberId
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails user) {

			String username = user.getUsername();

			log.debug("logoutHandler 실행, username: {}", username);
			// 로그인하지 않은 사용자가 로그아웃 요청 시, 아무것도 하지 않음
			// 권한 필요 없는 uri에서 로그아웃 요청 시, username == anonymousUser, 아무것도 하지 않음
			if (!username.equals("anonymousUser")) {

				log.debug("로그인한 사용자의 로그아웃 요청, logoutService 진행, userId: {}", username);
				Long memberId = Long.parseLong(username);
				loginService.logout(memberId);
				cookieUtils.deleteCookieBy(CookieType.ACCESS_TOKEN);
				cookieUtils.deleteCookieBy(CookieType.REFRESH_TOKEN);


			}
		}
	}

}

