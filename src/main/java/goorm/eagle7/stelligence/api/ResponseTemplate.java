package goorm.eagle7.stelligence.api;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;

/**
 * API 응답을 위한 클래스
 * @param <T> 응답 결과로 포함될 데이터의 타입
 */
@Getter
@JsonPropertyOrder({"success", "message", "results"}) // JSON으로 변환할 때, 이 순서대로 변환합니다.
public class ResponseTemplate<T> {
	private final boolean success;
	private final String message;
	private final T results;

	/**
	 * ApiResponse 생성자
	 * 인스턴스화는 오직 정적 팩토리 메소드를 통해서만 수행합니다.
	 */
	private ResponseTemplate(boolean success, String message, T results) {
		this.success = success;
		this.message = message;
		this.results = results;
	}

	public static <T> ResponseTemplate<T> ok() {
		return new ResponseTemplate<>(true, "요청에 성공했습니다.", null);
	}

	public static <T> ResponseTemplate<T> ok(T results) {
		return new ResponseTemplate<>(true, "요청에 성공했습니다.", results);
	}

	public static <T> ResponseTemplate<T> ok(T results, String message) {
		return new ResponseTemplate<>(true, message, results);
	}

	public static <T> ResponseTemplate<T> fail(String message) {
		return new ResponseTemplate<>(false, message, null);
	}
}
