package goorm.eagle7.stelligence.domain.notification;

import java.lang.reflect.Field;

import goorm.eagle7.stelligence.domain.notification.model.Notification;

public class TestNotification extends Notification {
	// TestNotification의 Builder 클래스
	public static class Builder {
		private TestNotification notification;

		public Builder() {
			notification = new TestNotification();
			id(1L);
			message("test message");
			uri("/test uri");
			isRead(false);
			memberId(null);
		}

		public Builder id(Long id) {
			return setField("id", id);
		}

		public Builder message(String message) {
			return setField("message", message);
		}

		public Builder uri(String uri) {
			return setField("uri", uri);
		}

		public Builder isRead(boolean isRead) {
			return setField("isRead", isRead);
		}

		public Builder memberId(Long memberId) {
			return setField("memberId", memberId);
		}

		// 다른 필드 설정 메소드...

		private Builder setField(String fieldName, Object value) {
			try {
				Field field = Notification.class.getDeclaredField(fieldName);
				field.setAccessible(true);
				field.set(notification, value);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			return this;
		}

		public TestNotification build() {
			return notification;
		}
	}

	// TestNotification에 대한 Builder 객체를 생성하는 정적 메소드
	public static Builder builder() {
		return new Builder();
	}
}
