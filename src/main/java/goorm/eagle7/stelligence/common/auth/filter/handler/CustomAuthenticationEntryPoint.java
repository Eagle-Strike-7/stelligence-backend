package goorm.eagle7.stelligence.common.auth.filter.handler;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.common.auth.filter.ResponseTemplateUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * AuthenticationException 처리하는 handler
 * 	- 유효한 자격증명을 제공하지 않고 접근하려 할때 401 응답을 ResponseTemplate 형태로 반환
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException authException) throws IOException {

		// 유효한 자격증명을 제공하지 않고 접근하려 할때 401 - login 기능 필요한 곳에서 하지 않을 때
		ResponseTemplate<Void> responseTemplate = ResponseTemplate.fail(authException.getMessage());
		ResponseTemplateUtils.toErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, responseTemplate);
	}
}

