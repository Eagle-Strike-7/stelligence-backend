package goorm.eagle7.stelligence.domain.document.content.model;

import static jakarta.persistence.GenerationType.*;

import java.util.ArrayList;
import java.util.List;

import goorm.eagle7.stelligence.common.entity.BaseTimeEntity;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.section.model.Section;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Document Entity
 * 문서를 저장하기 위한 엔티티 클래스입니다.
 * 문서는 여러 Section을 가질 수 있습니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Document extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "document_id")
	private Long id;

	/**
	 * 문서를 최초 생성한 사용자입니다.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "author_id")
	private Member author;

	private String title;

	/**
	 * 현재 버전을 나타냅니다.
	 */
	private Long currentRevision;

	/**
	 * 문서에 포함되어있는 섹션 목록입니다.
	 */
	@OneToMany(mappedBy = "document", orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	//===생성===//
	public static Document createDocument(String title, Member author) {
		Document document = new Document();
		document.title = title;
		document.author = author;
		document.currentRevision = 1L; //최초 생성 시 버전은 1입니다.
		return document;
	}

	//===수정===//

	/**
	 * 현재 버전을 증가시킵니다.
	 * Merge의 수행 이후 호출되어야 합니다.
	 */
	public void incrementCurrentRevision() {
		this.currentRevision++;
	}

}
