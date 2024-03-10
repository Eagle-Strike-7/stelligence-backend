package goorm.eagle7.stelligence.common.util;

import static java.nio.charset.StandardCharsets.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class ResponseUtilsTest {

	@Test
	@DisplayName("sendErrorResponse test")
	void sendErrorResponse() throws IOException {
		//given
		int httpStatusCode = 400;
		HttpServletResponse mockHttpServletResponse = mock(HttpServletResponse.class);
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);

		//when
		when(mockHttpServletResponse.isCommitted()).thenReturn(false);
		when(mockHttpServletResponse.getWriter()).thenReturn(printWriter);

		try (MockedStatic<RequestScopeUtils> mockedStatic = mockStatic(RequestScopeUtils.class)) {
			mockedStatic.when(RequestScopeUtils::getHttpServletResponse).thenReturn(mockHttpServletResponse);
			ResponseUtils.sendErrorResponse(httpStatusCode, "error");

			//then
			verify(mockHttpServletResponse).setStatus(httpStatusCode);
			verify(mockHttpServletResponse).setContentType(MediaType.APPLICATION_JSON_VALUE);
			verify(mockHttpServletResponse).setCharacterEncoding(UTF_8.name());
			assertThat(stringWriter.toString()).hasToString(
				"{\"success\":false,\"message\":\"error\",\"results\":null}");
		}
	}

	@Test
	@DisplayName("sendSuccessResponse(data, message) test")
	void sendSuccessResponse() throws IOException {
		//given
		HttpServletResponse mockHttpServletResponse = mock(HttpServletResponse.class);
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);

		//when
		when(mockHttpServletResponse.isCommitted()).thenReturn(false);
		when(mockHttpServletResponse.getWriter()).thenReturn(printWriter);

		try (MockedStatic<RequestScopeUtils> mockedStatic = mockStatic(RequestScopeUtils.class)) {
			mockedStatic.when(RequestScopeUtils::getHttpServletResponse).thenReturn(mockHttpServletResponse);
			ResponseUtils.sendSuccessResponse("data", "message");

			//then
			verify(mockHttpServletResponse).setStatus(HttpServletResponse.SC_OK);
			verify(mockHttpServletResponse).setContentType(MediaType.APPLICATION_JSON_VALUE);
			verify(mockHttpServletResponse).setCharacterEncoding(UTF_8.name());
			assertThat(stringWriter.toString()).hasToString(
				"{\"success\":true,\"message\":\"message\",\"results\":\"data\"}");
		}
	}

	@Test
	@DisplayName("sendSuccessResponse test")
	void sendSuccessResponseNoParameter() throws IOException {
		HttpServletResponse mockHttpServletResponse = mock(HttpServletResponse.class);
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);

		//when
		when(mockHttpServletResponse.isCommitted()).thenReturn(false);
		when(mockHttpServletResponse.getWriter()).thenReturn(printWriter);

		try (MockedStatic<RequestScopeUtils> mockedStatic = mockStatic(RequestScopeUtils.class)) {
			mockedStatic.when(RequestScopeUtils::getHttpServletResponse).thenReturn(mockHttpServletResponse);
			ResponseUtils.sendSuccessResponse();

			//then
			verify(mockHttpServletResponse).setStatus(HttpServletResponse.SC_OK);
			verify(mockHttpServletResponse).setContentType(MediaType.APPLICATION_JSON_VALUE);
			verify(mockHttpServletResponse).setCharacterEncoding(UTF_8.name());
			assertThat(stringWriter.toString()).hasToString(
				"{\"success\":true,\"message\":\"요청에 성공했습니다.\",\"results\":null}");
		}
	}
}