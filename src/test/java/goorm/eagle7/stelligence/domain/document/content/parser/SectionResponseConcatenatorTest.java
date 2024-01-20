package goorm.eagle7.stelligence.domain.document.content.parser;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import goorm.eagle7.stelligence.domain.document.content.dto.SectionResponse;
import goorm.eagle7.stelligence.domain.section.model.Heading;

class SectionResponseConcatenatorTest {

	@Test
	void concat() {
		List<SectionResponse> sections = new ArrayList<>();

		sections.add(SectionResponse.of(1L, 1L, Heading.H1, "마리모", "마리모는 관상용 식물이다.\n"));
		sections.add(SectionResponse.of(2L, 1L, Heading.H2, "마리모의 특징", "마리모는 물속에서 자란다.\n\n\n물을 아주 좋아한다.\n"));
		sections.add(SectionResponse.of(3L, 1L, Heading.H3, "마리모의 사육", "마리모는 물속에서 자란다.\n물을 자주 갈아줘야한다.\n"));
		sections.add(SectionResponse.of(4L, 1L, Heading.H3, "마리모의 장점", "마리모는 기분이 좋으면 물에 뜬다.\n"));

		String concatContent = SectionResponseConcatenator.concat(sections);

		System.out.println(concatContent);

		Assertions.assertThat(concatContent).isEqualTo(
			"# 마리모\n"
				+ "마리모는 관상용 식물이다.\n"
				+ "## 마리모의 특징\n"
				+ "마리모는 물속에서 자란다.\n\n\n"
				+ "물을 아주 좋아한다.\n"
				+ "### 마리모의 사육\n"
				+ "마리모는 물속에서 자란다.\n"
				+ "물을 자주 갈아줘야한다.\n"
				+ "### 마리모의 장점\n"
				+ "마리모는 기분이 좋으면 물에 뜬다.\n"
		);
	}

}