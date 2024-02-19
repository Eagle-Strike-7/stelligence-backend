package goorm.eagle7.stelligence.api.log.requestbodyfilter;

class RequestBodySlicingFormatter implements RequestBodyFormatter {

	private final int MAX_BODY_PRINT_LENGTH;

	public RequestBodySlicingFormatter(int maxBodyPrintLength) {
		this.MAX_BODY_PRINT_LENGTH = maxBodyPrintLength;
	}

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
