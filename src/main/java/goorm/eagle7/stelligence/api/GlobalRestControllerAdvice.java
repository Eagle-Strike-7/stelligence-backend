package goorm.eagle7.stelligence.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import goorm.eagle7.stelligence.api.exception.BaseException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 모든 Controller 에서 발생하는 예외를 처리하기 위한 클래스
 */
@Slf4j
@RestControllerAdvice(basePackages = "goorm.eagle7.stelligence")
public class GlobalRestControllerAdvice {

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public ResponseTemplate<String> handleException(Exception ex) {
		log.error("Exception catched in RestControllerAdvice : {}", ex.getMessage());
		ex.printStackTrace();
		return ResponseTemplate.fail(ex.getMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(BaseException.class)
	public ResponseTemplate<String> handleBaseException(BaseException ex) {
		log.debug("Exception catched in RestControllerAdvice : {}", ex.getMessage());
		return ResponseTemplate.fail(ex.getMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseTemplate<List<ValidationErrorResponse>> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException ex) {
		List<ValidationErrorResponse> validationErrorMessages = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(ValidationErrorResponse::of)
			.toList();

		return ResponseTemplate.fail(validationErrorMessages, "입력값이 올바르지 않습니다.");
	}

	@Getter
	@AllArgsConstructor
	public static class ValidationErrorResponse {
		private final String field;
		private final String message;

		public static ValidationErrorResponse of(FieldError error) {
			return new ValidationErrorResponse(error.getField(), error.getDefaultMessage());
		}
	}
}
