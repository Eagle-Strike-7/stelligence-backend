package goorm.eagle7.stelligence.api.log.requestbodyfilter;

/**
 * RequestLoggingFilter에서 Request Body를 필터링 하는 로직을 정의합니다.
 */
public interface RequestBodyFormatter {

	/**
	 * Request Body를 필터링합니다.
	 * @param requestBody Request Body
	 * @return 필터링된 Request Body
	 */
	String format(String requestBody);
}
