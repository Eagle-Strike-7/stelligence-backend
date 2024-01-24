package goorm.eagle7.stelligence.common.auth.oauth.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.common.login.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2LogoutCustomHandler implements LogoutHandler {

	private final LoginService loginService;

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
		if (authentication.getPrincipal() instanceof User user) {
			Long memberId = Long.parseLong(user.getUsername());
			loginService.logout(memberId);
		}
	}
}

