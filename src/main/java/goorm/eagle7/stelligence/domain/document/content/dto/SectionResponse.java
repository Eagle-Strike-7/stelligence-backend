package goorm.eagle7.stelligence.domain.document.content.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import goorm.eagle7.stelligence.domain.section.model.Heading;
import goorm.eagle7.stelligence.domain.section.model.Section;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Section의 정보를 담기 위한 응답 DTO입니다.
 */
@Getter
@ToString
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

	private SectionResponse(Long sectionId, Long revision, Heading heading, String title, String content) {
		this.sectionId = sectionId;
		this.revision = revision;
		this.heading = heading;
		this.title = title;
		this.content = content;
	}

	public static SectionResponse of(Section section) {
		return new SectionResponse(section);
	}

	public static SectionResponse of(Long sectionId, Long revision, Heading heading, String title, String content) {
		return new SectionResponse(sectionId, revision, heading, title, content);
	}

	/**
	 * 섹션의 내용을 하나의 문자열로 합칩니다.
	 * @return
	 */
	@JsonIgnore
	public String getFullContentString() {
		return heading.getSymbol() + " " + title + "\n" + content;
	}
}