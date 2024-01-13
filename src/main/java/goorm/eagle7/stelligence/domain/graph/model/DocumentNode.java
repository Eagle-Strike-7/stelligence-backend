package goorm.eagle7.stelligence.domain.graph.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DocumentNode 클래스는 neo4j에 저장될 문서 노드를 정의합니다.
 */
@Node(labels = "DocumentNode")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocumentNode {

	/**
	 * rdbId 프로퍼티는 rdb에 저장된 실제 문서의 id와 동일한 값을 가져야합니다.
	 * rdbId를 이용해 실제 문서의 내용에 접근합니다.
	 */
	@Id
	private Long documentId;

	/**
	 * title 프로퍼티는 문서의 제목을 나타냅니다.
	 * 그래프에 보여주거나, 제목을 이용해 검색을 할 때 사용됩니다.
	 */
	private String title;

	/**
	 * group 프로퍼티는 최상위 노드의 title을 나타냅니다.
	 * group을 이용해 그래프 뷰의 각 노드의 색을 구분하는 등의 작업이 가능합니다.
	 */
	private String group;

	/**
	 * HAS_CHILD 릴레이션은 하위 계층의 문서와의 관계를 나타냅니다.
	 * 여기서는 Relationship.Direction.INCOMING으로 설정힘으로써 어떤 노드를 부모노드로 설정할지를 결정합니다.
	 */
	@Relationship(type = "HAS_CHILD", direction = Relationship.Direction.INCOMING)
	private DocumentNode parentDocumentNode;

	/**
	 * 최상위 문서가 될 노드를 생성할 때 사용하는 생성자입니다.
	 * @param documentId: 문서의 RDB PK
	 * @param title: 문서의 제목
	 */
	public DocumentNode(Long documentId, String title) {
		this.documentId = documentId;
		this.title = title;
		// 부모 노드가 없다면, 그룹은 노드 자신의 title이 됩니다.
		this.group = title;
	}

	/**
	 * 특정 문서의 하위 계층의 문서를 생성할 때 사용하는 생성자입니다.
	 * @param documentId: 문서의 RDB PK
	 * @param title: 문서의 제목
	 * @param parentDocumentNode: 부모 문서
	 */
	public DocumentNode(Long documentId, String title, DocumentNode parentDocumentNode) {
		this.documentId = documentId;
		this.title = title;
		this.parentDocumentNode = parentDocumentNode;
		// 자식 노드의 그룹은 부모 노드의 그룹을 그대로 물려받습니다.
		this.group = parentDocumentNode.getGroup();
	}
}
