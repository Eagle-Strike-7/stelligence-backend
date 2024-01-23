package goorm.eagle7.stelligence.domain.document.graph;

import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import goorm.eagle7.stelligence.domain.document.graph.dto.DocumentNodeResponse;
import goorm.eagle7.stelligence.domain.document.graph.dto.HasChildRelationshipResponse;
import goorm.eagle7.stelligence.domain.document.graph.model.DocumentNode;

public interface DocumentNodeRepository extends Neo4jRepository<DocumentNode, Long> {

	@Query("match (n) where n.documentId=$documentId return n")
	Optional<DocumentNode> findSingleNodeByDocumentId(@Param("documentId") Long documentId);

	@Query("match (n:DocumentNode) return n.documentId as documentId, n.title as title, n.group as group")
	List<DocumentNodeResponse> findAllDocumentNode();

	@Query("match ()-[r:HAS_CHILD]->() return id(r) as linkId, startNode(r).documentId as parentDocumentId, endNode(r).documentId as childDocumentId")
	List<HasChildRelationshipResponse> findAllHasChildRelationship();

	// $depth가 mapping 되지 않아 :#{literal(#depth)}을 사용
	@Query("match (n1:DocumentNode)-[:HAS_CHILD*0..:#{literal(#depth)}]->(n2:DocumentNode)"
		+ " where n1.documentId=$documentId"
		+ " return n2.documentId as documentId, n2.title as title, n2.group as group")
	List<DocumentNodeResponse> findDocumentNodeByDocumentIdWithDepth(@Param("documentId") Long documentId, @Param("depth") int depth);

	// $depth가 mapping 되지 않아 :#{literal(#depth)}을 사용
	@Query("match (n1:DocumentNode)-[r:HAS_CHILD*1..:#{literal(#depth)}]->(n2:DocumentNode)"
		+ " where n1.documentId=$documentId"
		+ " return id(r[-1]) as linkId, startNode(r[-1]).documentId as parentDocumentId, endNode(r[-1]).documentId as childDocumentId")
	List<HasChildRelationshipResponse> findHasChildRelationshipByDocumentIdWithDepth(@Param("documentId") Long documentId, @Param("depth") int depth);


	// @Query("match (n:DocumentNode) where n.title contains $title return n.documentId, n.title, n.group limit $limit")
	@Query("call db.index.fulltext.queryNodes('documentTitleIndex', '*'+$title+'*', {limit: :#{literal(#limit)}, sortBy: 'score'})"
		+ " yield node, score"
		+ " where score > 0"
		+ " return node.documentId as documentId, node.title as title, node.group as group")
	List<DocumentNodeResponse> findNodeByTitle(@Param("title") String title, @Param("limit") int limit);

	@Query("match (n:DocumentNode)"
		+ " where n.documentId in $idList"
		+ " return n.documentId as documentId, n.title as title, n.group as group")
	List<DocumentNodeResponse> findNodeByDocumentId(@Param("idList") List<Long> documentIdList);

	@Query("match (n1:DocumentNode)-[:HAS_CHILD*0..:#{literal(#depth)}]->(n2:DocumentNode)"
		+ " where not exists((n1)<--())"
		+ " return n2.documentId as documentId, n2.title as title, n2.group as group")
	List<DocumentNodeResponse> findDocumentNodeFromRootWithDepth(@Param("depth") int depth);

	@Query("match (n1:DocumentNode)-[r:HAS_CHILD*1..:#{literal(#depth)}]->(n2:DocumentNode)"
		+ " where not exists((n1)<--())"
		+ " return id(r[-1]) as linkId, startNode(r[-1]).documentId as parentDocumentId, endNode(r[-1]).documentId as childDocumentId")
	List<HasChildRelationshipResponse> findHasChildRelationshipFromRootWithDepth(@Param("depth") int depth);


	/**
	 * 해당 노드가 루트 노드인지를 확인하는 메서드
	 * @param documentId: 루트 노드인지를 확인할 노드의 ID
	 * @return Optional&lt;Boolean&gt;: empty이면 해당 노드가 존재하지 않음을 뜻함
	 */
	@Query("match (n:DocumentNode)"
		+ " where n.documentId = $documentId"
		+ " return"
		+ "     case"
		+ "         when not exists {match (parent:DocumentNode)-[:HAS_CHILD]->(n)} then true"
		+ "         else false"
		+ "     end as isRootNode")
	Optional<Boolean> isRootNode(@Param("documentId") Long documentId);

	/**
	 * 루트 노드를 삭제하는데 사용되는 메서드.
	 * 루트 노드가 삭제되면 모든 자식 노드가 루트 노드가 되고,
	 * 모든 후손 노드의 그룹이 적절히 업데이트 됩니다.
	 * @param documentId: 삭제할 문서의 ID
	 */
	@Query("match (deleteNode:DocumentNode)"
		+ " where deleteNode.documentId = $documentId"
		+ " optional match (deleteNode)-[:HAS_CHILD]->(child:DocumentNode)"
		+ " "
		+ " with deleteNode, child"
		+ " match (child)-[:HAS_CHILD*0..]->(descendant:DocumentNode)"
		+ " set descendant.group = child.title"
		+ " detach delete deleteNode")
	void deleteRootNodeByDocumentId(@Param("documentId") Long documentId);

	/**
	 * 루트 노드가 아닌 노드를 삭제하는데 사용되는 메서드.
	 * 삭제할 노드의 자식 노드들은, 삭제할 노드의 기존 부모 노드와 직접 연결됩니다.
	 * @param documentId: 삭제할 문서의 ID
	 */
	@Query("match (deleteNode:DocumentNode)"
		+ " where deleteNode.documentId = $documentId"
		+ " match (parent:DocumentNode)-[:HAS_CHILD]->(deleteNode)"
		+ " optional match (deleteNode)-[:HAS_CHILD]->(child:DocumentNode)"
		+ " "
		+ " with deleteNode, parent, child"
		+ " merge (parent)-[:HAS_CHILD]->(child)"
		+ " detach delete deleteNode")
	void deleteNonrootNodeByDocumentId(@Param("documentId") Long documentId);

	/**
	 * documentId를 갖는 노드의 기존 부모 관계를 삭제하고,
	 * 새로운 부모 노드로 링크를 연결합니다.
	 * 이후, 업데이트된 노드와 하위 노드의 그룹을 업데이트 합니다.
	 * @param documentId: 링크를 수정할 문서의 ID
	 * @param parentDocumentId: 새로운 부모 노드의 ID
	 */
	@Query("match (:DocumentNode)-[r:HAS_CHILD]->(n:DocumentNode)"
		+ " where n.documentId = $documentId"
		+ " delete r"
		+ " "
		+ " with n"
		+ " match (parent:DocumentNode)"
		+ " where parent.documentId = $parentDocumentId"
		+ " merge (parent)-[:HAS_CHILD]->(n)"
		+ " "
		+ " with n, parent"
		+ " match (n)-[:HAS_CHILD*0..]->(descendant:DocumentNode)"
		+ " set descendant.group = parent.group")
	void changeLinkToUpdateParent(@Param("documentId") Long documentId, @Param("parentDocumentId") Long parentDocumentId);

	/**
	 * documentId를 갖는 노드의 기존 부모 관계를 삭제하고,
	 * 업데이트된 노드와 하위 노드의 그룹을 업데이트 합니다.
	 * @param documentId: 링크를 삭제할 문서의 ID
	 */
	@Query("match (:DocumentNode)-[r:HAS_CHILD]->(n:DocumentNode)"
		+ " where n.documentId = $documentId"
		+ " delete r"
		+ " "
		+ " with n"
		+ " match (n)-[:HAS_CHILD*0..]->(descendant:DocumentNode)"
		+ " set descendant.group = n.title")
	void removeLink(@Param("documentId") Long documentId);
}
