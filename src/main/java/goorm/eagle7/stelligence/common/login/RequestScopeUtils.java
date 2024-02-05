package goorm.eagle7.stelligence.common.login;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <h2>RequestScopeUtils - 현재 request, response.</h2>
 * <p>RequestContextHolder를 이용해 request, response를 가져온다.</p>
 * <p> request가 없으면 IllegalStateException 반환</p>
 */
public final class RequestScopeUtils {

	private RequestScopeUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * <h2>현재 스레드의 HttpServletRequest 가져오기</h2>
	 * @return HttpServletRequest
	 * @throws IllegalStateException request가 없으면 IllegalStateException 반환 // TODO Filter에서 request가 없을 때 처리
	 */
	public static HttpServletRequest getHttpServletRequest() {
		return ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
	}

	/**
	 * <h2>현재 스레드의 HttpServletResponse 가져오기</h2>
	 * @return HttpServletResponse
	 * @throws IllegalStateException request가 없으면 IllegalStateException 반환
	 */
	public static HttpServletResponse getHttpServletResponse() {
		return ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getResponse();
	}
}
