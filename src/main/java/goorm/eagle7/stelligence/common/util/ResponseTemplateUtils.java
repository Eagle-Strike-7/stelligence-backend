package goorm.eagle7.stelligence.common.util;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


/**
 *
 * 응답이 커밋되었는지 확인 후 response에 ResponseTemplate 형식으로 가공해 응답 전송
 * 	-> 커밋 이후에는 응답을 수정하거나 추가 데이터를 보낼 수 없음
 * 	-> 응답이 이미 커밋된 경우, 대부분의 상황에서는 아무런 조치를 취하지 않음.
 *
 */
@Slf4j
public class ResponseTemplateUtils {

	private ResponseTemplateUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static void toErrorResponse(HttpServletResponse response, int httpStatusCode, ResponseTemplate<Void> responseTemplate) {
		try {
			if (!response.isCommitted()) {
				// JSON으로 변환
				String jsonResponse = new ObjectMapper().writeValueAsString(responseTemplate);
				// 응답 설정
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.setStatus(httpStatusCode); // 401 상태 코드
				response.getWriter().write(jsonResponse);
			}
		} catch (IOException ex) {
			log.error("Error writing JSON response", ex);
			// 클라이언트에게 일반적인 오류 메시지 전송
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
			} catch (IOException e) {
				log.error("Error sending error response", e);
			}
		}
	}

	public static <T> void toSuccessResponse(HttpServletResponse response, ResponseTemplate<T> responseTemplate) {
		try {
			if (!response.isCommitted()) {
				// JSON으로 변환
				String jsonResponse = new ObjectMapper().writeValueAsString(responseTemplate);
				// 응답 설정
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.setStatus(HttpServletResponse.SC_OK);
				response.getWriter().write(jsonResponse);
			}
		} catch (IOException ex) {
			log.error("Error writing JSON response", ex);
			// 클라이언트에게 일반적인 오류 메시지 전송
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
			} catch (IOException e) {
				log.error("Error sending error response", e);
			}
		}
	}
}
