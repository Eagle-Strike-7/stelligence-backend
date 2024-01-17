package goorm.eagle7.stelligence.domain.document.content.dto;

import goorm.eagle7.stelligence.domain.section.model.Heading;
import goorm.eagle7.stelligence.domain.section.model.Section;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Section의 정보를 담기 위한 응답 DTO입니다.
 */
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SectionResponse {

	private Long sectionId;
	private Long revision;
	private Heading heading;
	private String title;
	private String content;

	private SectionResponse(Section section) {
		this.sectionId = section.getId();
		this.revision = section.getRevision();
		this.heading = section.getHeading();
		this.title = section.getTitle();
		this.content = section.getContent();
	}

	public static SectionResponse of(Section section) {
		return new SectionResponse(section);
	}
}
