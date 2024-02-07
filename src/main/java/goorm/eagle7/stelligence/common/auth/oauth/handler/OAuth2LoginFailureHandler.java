package goorm.eagle7.stelligence.common.auth.oauth.handler;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.common.util.ResponseTemplateUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	/**
	 * 로그인 실패 시 호출되는 메서드
	 * AuthenticationException 발생 시, 401 에러 반환
	 */
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception)  {

		ResponseTemplate<Void> responseTemplate = ResponseTemplate.fail(exception.getMessage());
		ResponseTemplateUtils.toErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, responseTemplate);

	}
}
