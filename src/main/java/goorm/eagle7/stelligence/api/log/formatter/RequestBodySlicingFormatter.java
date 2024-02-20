package goorm.eagle7.stelligence.api.log.formatter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class RequestBodySlicingFormatter implements RequestBodyFormatter {

	private final int MAX_BODY_PRINT_LENGTH;

	@Override
	public String format(String requestBody) {
		// 개행문자 제거
		requestBody = requestBody.replace("\n", " ");

		return requestBody.length() <= MAX_BODY_PRINT_LENGTH ? requestBody : sliceRequestBody(requestBody);
	}

	private String sliceRequestBody(String requestBody) {
		return requestBody.substring(0, MAX_BODY_PRINT_LENGTH) + "...";
	}
}
