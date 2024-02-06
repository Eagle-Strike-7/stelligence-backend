package goorm.eagle7.stelligence.domain.document.graph;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.domain.document.content.DocumentContentRepository;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.document.graph.dto.DocumentGraphResponse;
import goorm.eagle7.stelligence.domain.document.graph.dto.DocumentNodeResponse;
import goorm.eagle7.stelligence.domain.document.graph.dto.HasChildRelationshipResponse;
import goorm.eagle7.stelligence.domain.document.graph.model.DocumentNode;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Transactional
@Slf4j
class DocumentGraphServiceTest {

	@Autowired
	DocumentContentRepository documentContentRepository;
	@Autowired
	DocumentGraphService documentGraphService;
	@Autowired
	DocumentNodeRepository documentNodeRepository;
	@Autowired
	Neo4jClient neo4jClient;

	@Test
	@DisplayName("최상위 문서 노드 생성 서비스 테스트")
	void createDocumentNode() {

		Document createdDocument = Document.createDocument("제목1", member(null, "Paul"));
		documentContentRepository.save(createdDocument);

		documentGraphService.createDocumentNode(createdDocument);

		DocumentNode createdDocumentNode = documentNodeRepository.findById(createdDocument.getId()).get();
		log.info("createdDocumentNode = {}", createdDocumentNode);

		assertThat(createdDocumentNode.getDocumentId()).isEqualTo(createdDocument.getId());
		assertThat(createdDocumentNode.getTitle()).isEqualTo(createdDocument.getTitle());
		assertThat(createdDocumentNode.getGroup()).isEqualTo(createdDocument.getTitle());
		assertThat(createdDocumentNode.getParentDocumentNode()).isNull();
	}

	@Test
	@DisplayName("하위 문서 노드 생성 서비스 테스트")
	void createDocumentNodeWithParent() {

		// 기존에 존재하는 상위 문서
		Document parentDocument = Document.createDocument("상위 문서 제목", member(null, "Paul"));
		documentContentRepository.save(parentDocument);
		documentGraphService.createDocumentNode(parentDocument);

		// 하위 문서 생성
		Document createdDocument = Document.createDocument("하위 문서 제목", member(null, "Paul"));
		documentContentRepository.save(createdDocument);
		documentGraphService.createDocumentNodeWithParent(createdDocument, parentDocument.getId());

		DocumentNode createdDocumentNode = documentNodeRepository.findById(createdDocument.getId()).get();
		log.info("createdDocumentNode = {}", createdDocumentNode);

		assertThat(createdDocumentNode.getDocumentId()).isEqualTo(createdDocument.getId());
		assertThat(createdDocumentNode.getTitle()).isEqualTo(createdDocument.getTitle());
		assertThat(createdDocumentNode.getGroup()).isEqualTo(parentDocument.getTitle());
		assertThat(createdDocumentNode.getParentDocumentNode()).isNotNull();

	}

	@Test
	@DisplayName("특정 문서와 2의 깊이로 그래프 조회 서비스 테스트")
	void findGraphWithDepth2() {

		//given
		String[] queries = queriesThatMakesThreeNodesWithDepthFour();

		for (String queryString : queries) {
			neo4jClient.query(queryString).run();
		}

		//when
		DocumentGraphResponse graph = documentGraphService.findGraphWithDepth(1L, 2);
		Set<Long> documentIdSet = graph.getDocumentNodes()
			.stream()
			.map(DocumentNodeResponse::getDocumentId)
			.collect(Collectors.toSet());
		Set<Long> linkIdSet = graph.getLinks()
			.stream()
			.map(HasChildRelationshipResponse::getLinkId)
			.collect(Collectors.toSet());

		//then
		assertThat(graph.getDocumentNodes()).hasSize(1 + 3 + 9);
		assertThat(graph.getLinks()).hasSize(3 + 9);
		assertThat(documentIdSet).hasSize(1 + 3 + 9);
		assertThat(linkIdSet).hasSize(3 + 9);
	}

	@Test
	@DisplayName("특정 문서와 1의 깊이로 그래프 조회 서비스 테스트")
	void findGraphWithDepth1() {

		//given
		String[] queries = queriesThatMakesThreeNodesWithDepthFour();

		for (String queryString : queries) {
			neo4jClient.query(queryString).run();
		}

		//when
		DocumentGraphResponse graph2 = documentGraphService.findGraphWithDepth(1L, 1);
		Set<Long> documentIdSet2 = graph2.getDocumentNodes()
			.stream()
			.map(DocumentNodeResponse::getDocumentId)
			.collect(Collectors.toSet());
		Set<Long> linkIdSet2 = graph2.getLinks()
			.stream()
			.map(HasChildRelationshipResponse::getLinkId)
			.collect(Collectors.toSet());

		//then
		assertThat(graph2.getDocumentNodes()).hasSize(1 + 3);
		assertThat(graph2.getLinks()).hasSize(3);
		assertThat(documentIdSet2).hasSize(1 + 3);
		assertThat(linkIdSet2).hasSize(3);
	}

	@Test
	@DisplayName("특정 문서와 0의 깊이로 그래프 조회 서비스 테스트")
	void findGraphWithDepth0() {

		//given
		String[] queries = queriesThatMakesThreeNodesWithDepthFour();

		for (String queryString : queries) {
			neo4jClient.query(queryString).run();
		}

		//when
		DocumentGraphResponse graph3 = documentGraphService.findGraphWithDepth(1L, 0);
		Set<Long> documentIdSet3 = graph3.getDocumentNodes()
			.stream()
			.map(DocumentNodeResponse::getDocumentId)
			.collect(Collectors.toSet());
		Set<Long> linkIdSet3 = graph3.getLinks()
			.stream()
			.map(HasChildRelationshipResponse::getLinkId)
			.collect(Collectors.toSet());

		//then
		assertThat(graph3.getDocumentNodes()).hasSize(1);
		assertThat(graph3.getLinks()).isEmpty();
		assertThat(documentIdSet3).hasSize(1);
		assertThat(linkIdSet3).isEmpty();
	}

	@Test
	@DisplayName("특정 문서와 실제 깊이보다 큰 깊이로 그래프 조회 서비스 테스트")
	void findGraphWithBigDepth() {

		//given
		String[] queries = queriesThatMakesThreeNodesWithDepthFour();

		for (String queryString : queries) {
			neo4jClient.query(queryString).run();
		}

		//when
		DocumentGraphResponse graph4 = documentGraphService.findGraphWithDepth(1L, 99);
		Set<Long> documentIdSet4 = graph4.getDocumentNodes()
			.stream()
			.map(DocumentNodeResponse::getDocumentId)
			.collect(Collectors.toSet());
		Set<Long> linkIdSet4 = graph4.getLinks()
			.stream()
			.map(HasChildRelationshipResponse::getLinkId)
			.collect(Collectors.toSet());

		//then
		assertThat(graph4.getDocumentNodes()).hasSize(1 + 3 + 9 + 27);
		assertThat(graph4.getLinks()).hasSize(3 + 9 + 27);
		assertThat(documentIdSet4).hasSize(1 + 3 + 9 + 27);
		assertThat(linkIdSet4).hasSize(3 + 9 + 27);
	}

	@Test
	@DisplayName("전체 문서 그래프 조회 서비스 테스트")
	void findAllGraph() {

		//given
		String[] queries = queriesThatMakesThreeNodesWithDepthFour();

		for (String queryString : queries) {
			neo4jClient.query(queryString).run();
		}

		//when
		DocumentGraphResponse allGraph = documentGraphService.findAllGraph();
		Set<Long> documentIdSet = allGraph.getDocumentNodes()
			.stream()
			.map(DocumentNodeResponse::getDocumentId)
			.collect(Collectors.toSet());
		Set<Long> linkIdSet = allGraph.getLinks()
			.stream()
			.map(HasChildRelationshipResponse::getLinkId)
			.collect(Collectors.toSet());

		//then
		assertThat(allGraph.getDocumentNodes()).hasSize(120);
		assertThat(allGraph.getLinks()).hasSize(117);

		assertThat(documentIdSet).hasSize(120);
		assertThat(linkIdSet).hasSize(117);
	}

	@Test
	@DisplayName("최상위 문서와 2의 깊이로 그래프 조회 서비스 테스트")
	void findRootGraphWithDepth2() {

		//given
		final int depth = 2;
		final List<Long> rootNodeIdList = List.of(1L, 2L, 3L);
		String[] queries = queriesThatMakesThreeNodesWithDepthFour();

		for (String queryString : queries) {
			neo4jClient.query(queryString).run();
		}

		//when
		DocumentGraphResponse graph = documentGraphService.findFromRootNodesWithDepth(depth);
		Set<Long> documentIdSet = graph.getDocumentNodes()
			.stream()
			.map(DocumentNodeResponse::getDocumentId)
			.collect(Collectors.toSet());
		Set<Long> linkIdSet = graph.getLinks()
			.stream()
			.map(HasChildRelationshipResponse::getLinkId)
			.collect(Collectors.toSet());

		//then
		assertThat(graph.getDocumentNodes()).hasSize(3 + 9 + 27);
		assertThat(graph.getLinks()).hasSize(9 + 27);
		assertThat(documentIdSet).hasSize(3 + 9 + 27).containsAll(rootNodeIdList);
		assertThat(linkIdSet).hasSize(9 + 27);
	}

	@Test
	@DisplayName("최상위 문서와 0의 깊이로 그래프 조회 서비스 테스트")
	void findRootGraphWithDepth0() {

		//given
		final int depth = 0;
		final List<Long> rootNodeIdList = List.of(1L, 2L, 3L);
		String[] queries = queriesThatMakesThreeNodesWithDepthFour();

		for (String queryString : queries) {
			neo4jClient.query(queryString).run();
		}

		//when
		DocumentGraphResponse graph = documentGraphService.findFromRootNodesWithDepth(depth);
		Set<Long> documentIdSet = graph.getDocumentNodes()
			.stream()
			.map(DocumentNodeResponse::getDocumentId)
			.collect(Collectors.toSet());

		//then
		assertThat(graph.getDocumentNodes()).hasSize(3);
		assertThat(graph.getLinks()).isEmpty();
		assertThat(documentIdSet).hasSize(3).containsAll(rootNodeIdList);
	}

	@Test
	@DisplayName("루트 노드를 정상적으로 삭제할 수 있다.")
	void deleteRootNode() {
		// given
		final Long deleteTargetId = 1L;
		final List<Long> childIdListOfDeleteTarget = List.of(11L, 12L, 13L);

		String[] queries = queriesThatMakesThreeNodesWithDepthFour();

		for (String queryString : queries) {
			neo4jClient.query(queryString).run();
		}

		//when
		documentGraphService.deleteDocumentNode(deleteTargetId);

		//then
		assertThat(documentNodeRepository.findById(deleteTargetId)).isEmpty();

		List<DocumentNode> childNodeList = documentNodeRepository.findAllById(childIdListOfDeleteTarget);
		List<Boolean> isRootList = childNodeList.stream()
			.map(DocumentNode::getDocumentId)
			.map(id -> documentNodeRepository.isRootNode(id).get())
			.toList();

		assertThat(isRootList)
			.isNotEmpty()
			.allMatch(b -> b);
	}

	@Test
	@DisplayName("루트 노드가 아닌 노드를 정상적으로 삭제할 수 있다.")
	void deleteNonrootNode() {
		//given
		final Long deleteTargetId = 11L;
		final Long parentIdOfDeleteTargetId = 1L;
		final List<Long> childIdListOfDeleteTarget = List.of(111L, 112L, 113L);

		String[] queries = queriesThatMakesThreeNodesWithDepthFour();

		for (String queryString : queries) {
			neo4jClient.query(queryString).run();
		}

		//when
		documentGraphService.deleteDocumentNode(deleteTargetId);

		//then
		assertThat(documentNodeRepository.findById(deleteTargetId)).isEmpty();
		List<DocumentNode> childNodeList = documentNodeRepository.findAllById(childIdListOfDeleteTarget);
		List<DocumentNode> parentNodeList = childNodeList.stream().map(DocumentNode::getParentDocumentNode).toList();

		assertThat(parentNodeList)
			.isNotEmpty()
			.allMatch(p -> p.getDocumentId().equals(parentIdOfDeleteTargetId));
	}

	@Test
	@DisplayName("존재하지 않는 노드는 삭제할 수 없다.")
	void deleteNotExistNode() {
		//given
		final Long deleteTargetId = -1L;

		String[] queries = queriesThatMakesThreeNodesWithDepthFour();

		for (String queryString : queries) {
			neo4jClient.query(queryString).run();
		}

		//when - then
		assertThatThrownBy(() -> documentGraphService.deleteDocumentNode(deleteTargetId))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("링크 수정 테스트")
	void changeLinkToUpdateParent() {
		// given
		final Long updateTargetId = 11L;
		final List<Long> childIdListOfUpdateTarget = List.of(111L, 112L, 113L);
		final Long newParentNodeId = 3L;

		String[] queries = queriesThatMakesThreeNodesWithDepthFour();

		for (String queryString : queries) {
			neo4jClient.query(queryString).run();
		}

		//when
		documentGraphService.updateDocumentLink(updateTargetId, newParentNodeId);

		//then
		Optional<DocumentNode> targetNodeOptional = documentNodeRepository.findById(updateTargetId);
		assertThat(targetNodeOptional).isPresent();
		DocumentNode updateNode = targetNodeOptional.get();
		assertThat(updateNode.getParentDocumentNode().getDocumentId()).isEqualTo(newParentNodeId);
		assertThat(updateNode.getGroup()).isEqualTo(updateNode.getParentDocumentNode().getGroup());

		List<DocumentNodeResponse> childDocuments = documentNodeRepository.findNodeByDocumentId(
			childIdListOfUpdateTarget);
		assertThat(childDocuments)
			.isNotEmpty()
			.allMatch(n -> n.getGroup().equals(updateNode.getGroup()));
	}

	@Test
	@DisplayName("링크 삭제 테스트")
	void removeLink() {
		// given
		final Long updateTargetId = 11L;
		final List<Long> childIdListOfUpdateTarget = List.of(111L, 112L, 113L);

		String[] queries = queriesThatMakesThreeNodesWithDepthFour();

		for (String queryString : queries) {
			neo4jClient.query(queryString).run();
		}

		//when
		documentGraphService.updateDocumentLink(updateTargetId, null);

		//then
		Optional<DocumentNode> targetNodeOptional = documentNodeRepository.findById(updateTargetId);
		assertThat(targetNodeOptional).isPresent();
		DocumentNode targetNode = targetNodeOptional.get();
		assertThat(targetNode.getParentDocumentNode()).isNull();
		assertThat(targetNode.getGroup()).isEqualTo(targetNode.getTitle());

		List<DocumentNodeResponse> childDocuments = documentNodeRepository.findNodeByDocumentId(
			childIdListOfUpdateTarget);
		assertThat(childDocuments)
			.isNotEmpty()
			.allMatch(n -> n.getGroup().equals(targetNode.getGroup()));
	}

	@Test
	@DisplayName("루트아닌 노드 제목 수정 테스트")
	void updateNonrootNodeTitle() {
		// given
		final Long updateTargetId = 11L;
		final String updateTitle = "changedTitle";

		String[] queries = queriesThatMakesThreeNodesWithDepthFour();

		for (String queryString : queries) {
			neo4jClient.query(queryString).run();
		}

		//when
		documentGraphService.updateDocumentTitle(updateTargetId, updateTitle);

		//then
		DocumentNode documentNode = documentNodeRepository.findById(updateTargetId).get();
		assertThat(documentNode.getTitle()).isEqualTo(updateTitle);
		assertThat(documentNode.getGroup()).isNotEqualTo(updateTitle);
	}

	@Test
	@DisplayName("루트 노드 제목 수정 테스트")
	void updateRootNodeTitle() {
		// given
		final Long updateTargetId = 1L;
		final String updateTitle = "changedTitle";
		final List<Long> childIdListOfUpdateTarget = List.of(11L, 122L, 1333L);

		String[] queries = queriesThatMakesThreeNodesWithDepthFour();

		for (String queryString : queries) {
			neo4jClient.query(queryString).run();
		}

		//when
		documentGraphService.updateDocumentTitle(updateTargetId, updateTitle);

		//then
		DocumentNode documentNode = documentNodeRepository.findById(updateTargetId).get();
		List<DocumentNodeResponse> childDocuments = documentNodeRepository.findNodeByDocumentId(
			childIdListOfUpdateTarget);

		assertThat(documentNode.getTitle()).isEqualTo(updateTitle);
		assertThat(documentNode.getGroup()).isEqualTo(updateTitle);
		assertThat(childDocuments)
			.isNotEmpty()
			.allMatch(n -> n.getGroup().equals(updateTitle));
	}

	@Test
	@DisplayName("제목 변경 안함")
	void NoUpdateTitle() {
		// given
		final Long updateTargetId = 1L;
		final String updateTitle = "title1";

		String[] queries = queriesThatMakesThreeNodesWithDepthFour();

		for (String queryString : queries) {
			neo4jClient.query(queryString).run();
		}

		//when
		DocumentNode targetDocumentNode = documentNodeRepository.findById(updateTargetId).get();
		log.info("targetDocumentNode.getTitle(): {}", targetDocumentNode.getTitle());

		documentGraphService.updateDocumentTitle(updateTargetId, updateTitle);
		log.info("업데이트 쿼리 안나간 것을 확인");

		//then
		DocumentNode documentNode = documentNodeRepository.findById(updateTargetId).get();

		assertThat(documentNode.getTitle()).isEqualTo(updateTitle);
		assertThat(documentNode.getGroup()).isEqualTo(updateTitle);
	}

	private static String[] queriesThatMakesThreeNodesWithDepthFour() {
		return new String[] {
			"CREATE (:DocumentNode {documentId: 1, level: 2, title: 'title1', group: 'title1'}),"
				+ "       (:DocumentNode {documentId: 2, level: 2, title: 'title2', group: 'title2'}),"
				+ "       (:DocumentNode {documentId: 3, level: 2, title: 'title3', group: 'title3'});",
			"MATCH (p2:DocumentNode {level: 2})"
				+ " WITH p2"
				+ " CREATE (p2)-[:HAS_CHILD]->(:DocumentNode {documentId: p2.documentId * 10 + 1, level: 3, title: 'title' + toString(p2.documentId * 10 + 1), group: p2.group}),"
				+ "       (p2)-[:HAS_CHILD]->(:DocumentNode {documentId: p2.documentId * 10 + 2, level: 3, title: 'title' + toString(p2.documentId * 10 + 2), group: p2.group}),"
				+ "       (p2)-[:HAS_CHILD]->(:DocumentNode {documentId: p2.documentId * 10 + 3, level: 3, title: 'title' + toString(p2.documentId * 10 + 3), group: p2.group});",
			"MATCH (p3:DocumentNode {level: 3})"
				+ " WITH p3"
				+ " CREATE (p3)-[:HAS_CHILD]->(:DocumentNode {documentId: p3.documentId * 10 + 1, level: 4, title: 'title' + toString(p3.documentId * 10 + 1), group: p3.group}),"
				+ "       (p3)-[:HAS_CHILD]->(:DocumentNode {documentId: p3.documentId * 10 + 2, level: 4, title: 'title' + toString(p3.documentId * 10 + 2), group: p3.group}),"
				+ "       (p3)-[:HAS_CHILD]->(:DocumentNode {documentId: p3.documentId * 10 + 3, level: 4, title: 'title' + toString(p3.documentId * 10 + 3), group: p3.group});",
			" MATCH (p4:DocumentNode {level: 4})"
				+ " WITH p4"
				+ " CREATE (p4)-[:HAS_CHILD]->(:DocumentNode {documentId: p4.documentId * 10 + 1, level: 5, title: 'title' + toString(p4.documentId * 10 + 1), group: p4.group}),"
				+ "       (p4)-[:HAS_CHILD]->(:DocumentNode {documentId: p4.documentId * 10 + 2, level: 5, title: 'title' + toString(p4.documentId * 10 + 2), group: p4.group}),"
				+ "       (p4)-[:HAS_CHILD]->(:DocumentNode {documentId: p4.documentId * 10 + 3, level: 5, title: 'title' + toString(p4.documentId * 10 + 3), group: p4.group});",
			"MATCH (n:DocumentNode)"
				+ " SET n.level = NULL;"
		};
	}
}