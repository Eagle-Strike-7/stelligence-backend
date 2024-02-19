package goorm.eagle7.stelligence.common.util;

import static java.nio.charset.StandardCharsets.*;
import static org.springframework.http.MediaType.*;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 응답이 커밋되었는지 확인 후 response에 ResponseTemplate 형식으로 가공해 응답 전송
 * 	-> 커밋 이후에는 응답을 수정하거나 추가 데이터를 보낼 수 없음
 * 	-> 응답이 이미 커밋된 경우, 대부분의 상황에서는 아무런 조치를 취하지 않음.
 */
@Slf4j
public class ResponseUtils {

	private ResponseUtils() {
		throw new IllegalStateException("Utility class");
	}

	private static final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * 에러 응답을 전송합니다.
	 * @param httpStatusCode 상태 코드
	 * @param message 응답 메시지
	 */
	public static void sendErrorResponse(int httpStatusCode, String message) {
		sendResponse(httpStatusCode, ResponseTemplate.fail(message));
	}

	/**
	 * SuccessResponse를 보내는 상황 중 특별히 데이터를 지정해 줄 필요가 없을 때 사용합니다.
	 */
	public static void sendSuccessResponse() {
		sendSuccessResponse(null, "요청에 성공했습니다.");
	}

	/**
	 * 성공 응답을 전송합니다.
	 * @param data 응답 데이터 (optional)
	 * @param message 응답 메시지 (optional)
	 * @param <T> 응답 데이터 타입
	 */
	public static <T> void sendSuccessResponse(T data, String message) {
		sendResponse(HttpServletResponse.SC_OK, ResponseTemplate.ok(data, message));
	}

	/**
	 * 상태 코드와 응답 바디를 받아서
	 * 성공 케이스와 실패 케이스의 공통적인 부분을 줄이기 위한 메서드입니다.
	 * @param httpStatusCode 상태 코드
	 * @param responseBody 응답 바디
	 */
	private static void sendResponse(int httpStatusCode, Object responseBody) {
		HttpServletResponse response = RequestScopeUtils.getHttpServletResponse();

		if (!response.isCommitted()) { // 응답을 보낼 수 있는 상태인지 확인
			setHttpServletResponse(response, httpStatusCode, responseBody);
		} else {
			log.warn("Response already committed");
		}
	}

	/**
	 * Http Status Code와 Body를 받아서 Response에 응답 내용을 설정합니다.
	 * 응답 내용을 만드는데에 문제가 발생한 경우, 500 에러 응답을 전송합니다.
	 * @param httpStatusCode 상태 코드
	 * @param responseBody 응답 바디
	 */
	private static void setHttpServletResponse(HttpServletResponse response, int httpStatusCode, Object responseBody) {
		try {
			response.setContentType(APPLICATION_JSON_VALUE);
			response.setCharacterEncoding(UTF_8.name());
			response.setStatus(httpStatusCode); // 401 상태 코드
			response.getWriter().write(objectMapper.writeValueAsString(responseBody));
		} catch (IOException e) {
			log.error("Error writing JSON response", e);
			sendInternalServerError(response); // 500 에러 응답
		}
	}

	/**
	 * 500 에러 응답을 전송합니다.
	 * @param response HttpServletResponse
	 */
	private static void sendInternalServerError(HttpServletResponse response) {
		try {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
		} catch (IOException e) {
			log.error("Error sending error response", e);
		}
	}
}
