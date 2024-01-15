package goorm.eagle7.stelligence.domain.document.parser;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import goorm.eagle7.stelligence.domain.document.dto.SectionRequest;
import goorm.eagle7.stelligence.domain.section.model.Heading;

class DocumentParserTest {

	@Test
	@DisplayName("정상 요청 테스트")
	void parse() {
		//given
		String rawContent =
			"# title\n"
				+ "content\n"
				+ "## title2\n"
				+ "content2 line 1\n"
				+ "content2 line 2\n"
				+ "### title3\n"
				+ "content3";

		DocumentParser parser = new DocumentParser();

		//when
		List<SectionRequest> result = parser.parse(rawContent);

		//then
		assertThat(result).hasSize(3);

		assertThat(result.get(0).getHeading()).isEqualTo(Heading.H1);
		assertThat(result.get(0).getTitle()).isEqualTo("title");
		assertThat(result.get(0).getContent()).isEqualTo("content\n");

		assertThat(result.get(1).getHeading()).isEqualTo(Heading.H2);
		assertThat(result.get(1).getTitle()).isEqualTo("title2");
		assertThat(result.get(1).getContent()).isEqualTo("content2 line 1\ncontent2 line 2\n");

		assertThat(result.get(2).getHeading()).isEqualTo(Heading.H3);
		assertThat(result.get(2).getTitle()).isEqualTo("title3");
		assertThat(result.get(2).getContent()).isEqualTo("content3\n");
	}

	@Test
	@DisplayName("마지막 문단이 제목으로 끝나는 경우")
	void parse2() {
		//given
		String rawContent =
			"# title\n"
				+ "content\n"
				+ "## title2\n"
				+ "content2 line 1\n"
				+ "content2 line 2\n"
				+ "### title3\n";

		DocumentParser parser = new DocumentParser();

		//when
		List<SectionRequest> result = parser.parse(rawContent);

		//then
		assertThat(result).hasSize(3);

		assertThat(result.get(2).getHeading()).isEqualTo(Heading.H3);
		assertThat(result.get(2).getTitle()).isEqualTo("title3");
		assertThat(result.get(2).getContent()).isEmpty();
		assertThat(result.get(2).getContent()).isNotNull();
	}

	@Test
	@DisplayName("첫 문단이 제목으로 시작하지 않는 경우")
	void parse3() {
		//given
		String rawContent =
			"content\n"
				+ "## title2\n"
				+ "content2 line 1\n"
				+ "content2 line 2\n"
				+ "### title3\n"
				+ "content3";

		DocumentParser parser = new DocumentParser();

		//when
		List<SectionRequest> result = parser.parse(rawContent);

		//then
		assertThat(result).hasSize(2);

		//첫 문단은 제목이 없으므로 무시한다.

		assertThat(result.get(0).getHeading()).isEqualTo(Heading.H2);
		assertThat(result.get(0).getTitle()).isEqualTo("title2");
		assertThat(result.get(0).getContent()).isEqualTo("content2 line 1\ncontent2 line 2\n");

		assertThat(result.get(1).getHeading()).isEqualTo(Heading.H3);
		assertThat(result.get(1).getTitle()).isEqualTo("title3");
		assertThat(result.get(1).getContent()).isEqualTo("content3\n");
	}
}