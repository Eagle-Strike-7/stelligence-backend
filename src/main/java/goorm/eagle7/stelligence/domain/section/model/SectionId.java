package goorm.eagle7.stelligence.domain.section.model;

import static lombok.AccessLevel.*;

import java.io.Serializable;
import java.util.Objects;

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

	public static SectionId of(Long sectionId, Long revision) {
		return new SectionId(sectionId, revision);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		SectionId sectionId = (SectionId)o;
		return Objects.equals(id, sectionId.id) && Objects.equals(revision, sectionId.revision);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, revision);
	}
}
