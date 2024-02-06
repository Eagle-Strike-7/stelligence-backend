package goorm.eagle7.stelligence.domain.section.model;

import static jakarta.persistence.FetchType.*;
import static lombok.AccessLevel.*;

import goorm.eagle7.stelligence.common.entity.BaseTimeEntity;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Section
 * Document의 구성단위를 나타내며, 수정의 단위입니다.
 */
@Entity
@Getter
@IdClass(SectionId.class)
@NoArgsConstructor(access = PROTECTED)
public class Section extends BaseTimeEntity implements Comparable<Section> {

	@Id
	@Column(name = "section_id")
	private Long id;

	@Id
	private Long revision;

	//Document
	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "document_id")
	private Document document;

	@Enumerated(EnumType.STRING)
	private Heading heading;

	private String title;

	@Column(columnDefinition = "TEXT")
	private String content;

	@Column(name = "orders")
	private int order;

	//===생성===//
	public static Section createSection(
		Document document,
		Long id,
		Long revision,
		Heading heading,
		String title,
		String content,
		int order
	) {
		Section section = new Section();
		section.document = document;
		section.id = id;
		section.revision = revision;
		section.heading = heading;
		section.title = title;
		section.content = content;
		section.order = order;

		document.getSections().add(section);

		return section;
	}

	public void incrementOrder() {
		this.order++;
	}

	@Override
	public int compareTo(Section section) {
		return this.order - section.order;
	}

}
