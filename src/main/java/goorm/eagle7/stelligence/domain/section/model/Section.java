package goorm.eagle7.stelligence.domain.section.model;

import static lombok.AccessLevel.*;

import goorm.eagle7.stelligence.common.entity.BaseTimeEntity;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.member.model.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
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

	/**
	 * Section의 작성자입니다.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "author_id")
	private Member author;

	//Document
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "document_id")
	private Document document;

	@Enumerated(EnumType.STRING)
	private Heading heading;

	private String title;

	@Lob
	private String content;

	@Column(name = "orders")
	private int order;

	//===생성===//
	public static Section createSection(
		Document document,
		Member author,
		Long id,
		Long revision,
		Heading heading,
		String title,
		String content,
		int order
	) {
		Section section = new Section();
		section.document = document;
		section.author = author;
		section.id = id;
		section.revision = revision;
		section.heading = heading;
		section.title = title;
		section.content = content;
		section.order = order;

		document.getSections().add(section);

		return section;
	}

	@Override
	public int compareTo(Section section) {
		return this.order - section.order;
	}

}
