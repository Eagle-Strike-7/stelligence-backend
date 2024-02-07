package goorm.eagle7.stelligence.common.auth.filter.handler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.common.util.ResponseTemplateUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	/**
	 * AccessDeniedException 처리하는 handler
	 * 	- 권한 없는 사용자가 접근하려 할때 403 응답을 ResponseTemplate 형태로 반환
	 */
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) {

		// 필요한 권한이 없이 접근하려 할때 400으로 통일 - Admin, user, 다른 사람 정보를 수정하려 할 때 등
		log.debug("CustomAccessDeniedHandler 진입");
		ResponseTemplate<Void> responseTemplate = ResponseTemplate.fail(accessDeniedException.getMessage());
		ResponseTemplateUtils.toErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, responseTemplate);

	}
}
