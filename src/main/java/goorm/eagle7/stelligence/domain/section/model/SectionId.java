package goorm.eagle7.stelligence.domain.section.model;

import static lombok.AccessLevel.*;

import java.io.Serializable;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Section의 복합키를 구성하기 위한 클래스입니다.
 * Section은 sectionId와 revision을 복합키로 합니다.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
public class SectionId implements Serializable {

	@Column(name = "section_id")
	private Long id;
	private Long revision;

	private SectionId(Long id, Long revision) {
		this.id = id;
		this.revision = revision;
	}

	public static SectionId createSectionId(Long sectionId, Long revision) {
		return new SectionId(sectionId, revision);
	}
}
