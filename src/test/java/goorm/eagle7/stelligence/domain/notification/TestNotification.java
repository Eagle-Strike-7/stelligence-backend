package goorm.eagle7.stelligence.domain.notification;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import goorm.eagle7.stelligence.domain.notification.model.Notification;
import goorm.eagle7.stelligence.domain.notification.model.NotificationContent;

public class TestNotification {
	// TestNotification의 Builder 클래스
	public static class Builder {
		private Notification notification;

		public Builder() {
			try {
				Constructor<?> constructor = Notification.class.getDeclaredConstructor();
				constructor.setAccessible(true);
				notification = (Notification)constructor.newInstance();
				id(1L);
				content(null);
				isRead(false);
				memberId(null);
			} catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
					 InvocationTargetException e) {
				throw new RuntimeException(e);
			}

		}

		public Builder id(Long id) {
			return setField("id", id);
		}

		public Builder content(NotificationContent notificationContent) {
			return setField("content", notificationContent);
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

		public Notification build() {
			return notification;
		}
	}

	// TestNotification에 대한 Builder 객체를 생성하는 정적 메소드
	public static Builder builder() {
		return new Builder();
	}
}
