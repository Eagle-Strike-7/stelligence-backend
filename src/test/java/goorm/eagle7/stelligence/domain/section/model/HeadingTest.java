package goorm.eagle7.stelligence.domain.section.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class HeadingTest {

	@Test
	void getFullHeadingTag() {
		String tag = Heading.H1.getFullHeadingTag("testTitle");
		Assertions.assertThat(tag).isEqualTo("<h1>testTitle</h1>");
	}
}