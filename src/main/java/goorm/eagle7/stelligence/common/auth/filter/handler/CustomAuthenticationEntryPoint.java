package goorm.eagle7.stelligence.common.auth.filter.handler;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.common.util.ResponseTemplateUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * <h2>AuthenticationException 처리하는 handler</h2>
 * <p>- 유효한 자격증명을 제공하지 않고 접근하려 할때 처리</p>
 * <p>- 401 응답을 ResponseTemplate 형태로 반환</p>
 */
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private static final String ERROR_MESSAGE = "유효하지 않은 사용자입니다.";

	@Override
	public void commence(HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException authException){

		// 필요한 권한이 없이 접근하려 할때 401로 통일
		log.trace("CustomAuthenticationEntryPoint 진입");
		ResponseTemplate<Void> responseTemplate = ResponseTemplate.fail(ERROR_MESSAGE);
		ResponseTemplateUtils.toErrorResponse(HttpServletResponse.SC_UNAUTHORIZED, responseTemplate);

	}

}

