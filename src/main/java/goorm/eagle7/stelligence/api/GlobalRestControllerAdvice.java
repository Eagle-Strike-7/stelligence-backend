package goorm.eagle7.stelligence.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import goorm.eagle7.stelligence.api.exception.BaseException;
import lombok.extern.slf4j.Slf4j;

/**
 * 모든 Controller 에서 발생하는 예외를 처리하기 위한 클래스
 */
@Slf4j
@RestControllerAdvice(basePackages = "goorm.eagle7.stelligence")
public class GlobalRestControllerAdvice {

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public ApiResponse<String> handleException(Exception ex) {
		log.error("Exception catched in RestControllerAdvice : {}", ex.getMessage());
		return ApiResponse.fail(ex.getMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(BaseException.class)
	public ApiResponse<String> handleBaseException(BaseException ex) {
		log.debug("Exception catched in RestControllerAdvice : {}", ex.getMessage());
		return ApiResponse.fail(ex.getMessage());
	}
}
