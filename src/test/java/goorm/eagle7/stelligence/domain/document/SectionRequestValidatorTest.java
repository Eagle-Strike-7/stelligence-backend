package goorm.eagle7.stelligence.domain.document;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.document.content.SectionRequestValidator;
import goorm.eagle7.stelligence.domain.document.content.dto.SectionRequest;
import goorm.eagle7.stelligence.domain.section.model.Heading;

class SectionRequestValidatorTest {

	SectionRequestValidator sectionRequestValidator = new SectionRequestValidator();

	@Test
	@DisplayName("유효한 SectionRequest인 경우")
	void validSectionRequest() {
		//given
		List<SectionRequest> sectionRequests = List.of(
			new SectionRequest(Heading.H1, "title1", "content1"),
			new SectionRequest(Heading.H2, "title2", "content2"),
			new SectionRequest(Heading.H3, "title3", "content3")
		);

		//when
		sectionRequestValidator.validate(sectionRequests);

		//then
		Assertions.assertThat(sectionRequests).isNotEmpty();
	}

	@Test
	@DisplayName("Heading이 없는 경우")
	void noHeading() {
		//given
		List<SectionRequest> sectionRequests = List.of(
			new SectionRequest(null, "title", "content")
		);

		Assertions.assertThatThrownBy(() ->
				sectionRequestValidator.validate(sectionRequests))
			.isInstanceOf(BaseException.class)
			.hasMessage("Heading이 존재하지 않습니다.");
	}

	@Test
	@DisplayName("제목이 null인 경우")
	void nullTitle() {
		//given
		List<SectionRequest> sectionRequests = List.of(
			new SectionRequest(Heading.H1, null, "content")
		);

		Assertions.assertThatThrownBy(() ->
				sectionRequestValidator.validate(sectionRequests))
			.isInstanceOf(BaseException.class)
			.hasMessage("제목은 빈 값이면 안되며, 100자 이하로 입력해주세요.");
	}

	@Test
	@DisplayName("제목이 empty string인 경우")
	void emptyTitle() {
		//given
		List<SectionRequest> sectionRequests = List.of(
			new SectionRequest(Heading.H1, "", "content")
		);

		Assertions.assertThatThrownBy(() ->
				sectionRequestValidator.validate(sectionRequests))
			.isInstanceOf(BaseException.class)
			.hasMessage("제목은 빈 값이면 안되며, 100자 이하로 입력해주세요.");
	}

	@Test
	@DisplayName("제목이 blank인 경우")
	void blankTitle() {
		//given
		List<SectionRequest> sectionRequests = List.of(
			new SectionRequest(Heading.H1, "               ", "content")
		);

		Assertions.assertThatThrownBy(() ->
				sectionRequestValidator.validate(sectionRequests))
			.isInstanceOf(BaseException.class)
			.hasMessage("제목은 빈 값이면 안되며, 100자 이하로 입력해주세요.");
	}

	@Test
	@DisplayName("제목이 100자 초과인 경우")
	void over100Title() {
		//given
		List<SectionRequest> sectionRequests = List.of(
			new SectionRequest(Heading.H1, "a".repeat(101), "content")
		);

		Assertions.assertThatThrownBy(() ->
				sectionRequestValidator.validate(sectionRequests))
			.isInstanceOf(BaseException.class)
			.hasMessage("제목은 빈 값이면 안되며, 100자 이하로 입력해주세요.");
	}

	@Test
	@DisplayName("콘텐츠가 65536자 초과인 경우")
	void over65536Content() {
		//given
		List<SectionRequest> sectionRequests = List.of(
			new SectionRequest(Heading.H1, "title", "a".repeat(65537))
		);

		Assertions.assertThatThrownBy(() ->
				sectionRequestValidator.validate(sectionRequests))
			.isInstanceOf(BaseException.class)
			.hasMessage("Content는 65536자 이하로 입력해주세요.");
	}

}