package goorm.eagle7.stelligence.domain.graph.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
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
	 * id 프로퍼티는 neo4j가 내부적으로 사용하는 id를 정의합니다.
	 * id는 노드 삽입 시점에 UUIDStringGenerator를 이용해 유일한 값으로 생성됩니다.
	 */
	@Id
	@GeneratedValue(UUIDStringGenerator.class)
	private String nodeId;

	/**
	 * title 프로퍼티는 문서의 제목을 나타냅니다.
	 * 그래프에 보여주거나, 제목을 이용해 검색을 할 때 사용됩니다.
	 */
	private String title;

	/**
	 * rdbId 프로퍼티는 rdb에 저장된 실제 문서의 id와 동일한 값을 갖습니다.
	 * rdbId를 이용해 실제 문서의 내용에 접근합니다.
	 */
	private Long documentId;

	/**
	 * group 프로퍼티는 최상위 노드의 title을 나타냅니다.
	 * group을 이용해 그래프 뷰의 각 노드의 색을 구분하는 등의 작업이 가능합니다.
	 */
	private String group;

	/**
	 * HAS_CHILD 릴레이션은 하위 계층의 문서와의 관계를 나타냅니다.
	 */
	@Relationship(type = "HAS_CHILD", direction = Relationship.Direction.OUTGOING)
	private List<DocumentNode> childDocumentList = new ArrayList<>();

	public DocumentNode(String title, Long documentId, String group) {
		this.title = title;
		this.documentId = documentId;
		this.group = group;
	}
}
