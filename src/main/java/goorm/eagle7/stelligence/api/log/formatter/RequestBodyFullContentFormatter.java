package goorm.eagle7.stelligence.api.log.formatter;

/**
 * RequestBody의 전체를 로깅하는 프린터입니다.
 */
class RequestBodyFullContentFormatter implements RequestBodyFormatter {
	@Override
	public String format(String requestBody) {
		// 개행문자 제거
		requestBody = requestBody.replace("\n", " ");
		return requestBody;
	}
}
