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
	 * 루트 노드를 삭제하는데 사용되는 메서드
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
	 * 루트 노드가 아닌 노드를 삭제하는데 사용되는 메서드
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
}
