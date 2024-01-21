package goorm.eagle7.stelligence.domain.document.content.model;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import java.util.ArrayList;
import java.util.List;

import goorm.eagle7.stelligence.common.entity.BaseTimeEntity;
import goorm.eagle7.stelligence.domain.section.model.Section;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Document Entity
 * 문서를 저장하기 위한 엔티티 클래스입니다.
 * 문서는 여러 Section을 가질 수 있습니다.
 *
 * todo: 추후 작성자를 저장할 수 있어야 합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Document extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "document_id")
	private Long id;

	//Member (creator)

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
	public static Document createDocument(String title) {
		Document document = new Document();
		document.title = title;

		//최초 생성 시 버전은 1입니다.
		document.currentRevision = 1L;
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
