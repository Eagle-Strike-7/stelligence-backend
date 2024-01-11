package goorm.eagle7.stelligence.api.exception;

/**
 * 커스텀 예외 클래스
 * 비즈니스 적으로 관리해야하는 예외들의 조상 클래스입니다.
 * 컨트롤러에서 예외를 잡을 때에는 400번 에러로 처리합니다.
 */
public class BaseException extends RuntimeException {

	public BaseException(String message) {
		super(message);
	}
}
