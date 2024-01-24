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
	 * Document가 가지고 있는 Section들입니다. 이는 과거버전과 현재버전을 모두 담고 있습니다.
	 *
	 * 이 필드는 일반적인 상황에서 사용할 수 없습니다.
	 * Document의 버전이 올라감에 따라 새롭게 생성되는 섹션과 함께 기존의 섹션들도 모두 Document를 가리키기 때문에
	 * Document.sections 는 과거버전의 섹션들을 모두 가지고 있습니다.
	 *
	 * Document의 조회는 기본적으로 Document에 대한 기본적인 정보와 특정 버전에 따른 섹션들의 조합으로 이루어집니다.
	 *
	 * 따라서 모든 Section의 정보를 가지고 있는 Document.sections는 유효하게 사용되지 않을 뿐 더러
	 * 실수로 사용될 때 불필요한 성능 저하를 가져올 수 있습니다.
	 *
	 * 따라서 Document 엔티티에서 sections에 대한 참조를 끊는 것이 올바른 상황으로 보이나
	 * 이를 위해서는 많은 테스트 코드의 수정과 비즈니스 로직의 수정이 필요합니다.
	 *
	 * 따라서 현재는 sections를 유지하되, 이를 특수한 경우에만 사용하는 것으로 하고
	 * 추후에 sections를 제거하는 작업을 진행할 예정입니다.
	 */
	@OneToMany(mappedBy = "document")
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
