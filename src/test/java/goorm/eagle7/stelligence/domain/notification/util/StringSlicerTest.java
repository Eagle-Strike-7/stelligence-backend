package goorm.eagle7.stelligence.domain.notification.util;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StringSlicerTest {
	@Test
	@DisplayName("문자열이 null일 때 slice() 메서드를 호출하면 IllegalArgumentException이 발생한다.")
	void sliceNull() {
		assertThatThrownBy(() -> StringSlicer.slice(null))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("문자열이 null입니다.");
	}

	@Test
	void sliceShort() {
		String str = "12345678901234567890";
		assertThat(StringSlicer.slice(str)).isEqualTo(str);
	}

	@Test
	void sliceLong() {
		String str = "123456789012345678901234567890";
		assertThat(StringSlicer.slice(str)).isEqualTo("12345678901234567890...");
	}
}