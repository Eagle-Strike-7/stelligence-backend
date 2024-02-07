package goorm.eagle7.stelligence.common.auth.oauth.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.common.util.ResponseTemplateUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

// TODO HttpStatusReturningServerLogoutSuccessHandler 사용도 가능.
@Component
@RequiredArgsConstructor
public class OAuth2LogoutSuccessHandler implements LogoutSuccessHandler {

	/**
	 * 로그아웃 성공 시 호출되는 메서드
	 * 	- 로그아웃 성공 시, 200 응답 반환
	 */
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) {

		ResponseTemplate<Void> responseTemplate = ResponseTemplate.ok();
		ResponseTemplateUtils.toSuccessResponse(response, responseTemplate);
	}

}

