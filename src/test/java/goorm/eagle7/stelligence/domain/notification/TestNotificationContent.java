package goorm.eagle7.stelligence.domain.notification;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import goorm.eagle7.stelligence.domain.notification.model.NotificationContent;

public class TestNotificationContent {

	// TestNotificationContent의 Builder 클래스
	public static class Builder {
		private NotificationContent notificationContent;

		public Builder() {
			try {
				Constructor<?> constructor = NotificationContent.class.getDeclaredConstructor();
				constructor.setAccessible(true);
				notificationContent = (NotificationContent)constructor.newInstance();
				id(1L);
				message("test message");
				uri("/test uri");
			} catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
					 InvocationTargetException e) {
				throw new RuntimeException(e);
			}
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

		// 다른 필드 설정 메소드...

		private Builder setField(String fieldName, Object value) {
			try {
				Field field = NotificationContent.class.getDeclaredField(fieldName);
				field.setAccessible(true);
				field.set(notificationContent, value);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			return this;
		}

		public NotificationContent build() {
			return notificationContent;
		}

	}

	public static Builder builder() {
		return new Builder();
	}

}
