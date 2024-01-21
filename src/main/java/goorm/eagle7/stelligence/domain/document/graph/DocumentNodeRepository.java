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
	 * 문서 ID를 기준으로 삭제할 노드를 찾고, 삭제할 노드의 부모와 자식 노드도 찾음
	 * <p>
	 * 부모 노드가 null이라면 root 노드였다는 뜻이므로, 모든 자식노드를 루트노드로 승격시키고 모든 후손노드의 그룹을 업데이트
	 * <p>
	 * 부모 노드가 null이 아니라면, 모든 자식노드를 삭제할 노드의 부모노드의 자식으로 직접 연결해줌
	 * <p>
	 * 그 다음 삭제할 노드와 연결관계를 삭제
	 * @param documentId: 삭제할 문서 ID
	 */
	@Query("match (deleteNode:DocumentNode) where deleteNode.documentId = $documentId"
		+ " optional match (parent:DocumentNode)-[:HAS_CHILD]->(deleteNode)"
		+ " optional match (deleteNode)-[:HAS_CHILD]->(child:DocumentNode)"
		+ " "
		+ " with deleteNode, parent, child"
		+ " where parent is null"
		+ " match (child)->[:HAS_CHILD*0..]->(descendent:DocumentNode)"
		+ " set descendant.group = child.title"
		+ " detach delete deleteNode"
		+ " "
		+ " with deleteNode, parent, child"
		+ " where parent is not null"
		+ " merge (parent)-[:HAS_CHILD]->(child)"
		+ " detach delete deleteNode")
	void deleteByDocumentId(@Param("documentId") Long documentId);
}
