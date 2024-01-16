package goorm.eagle7.stelligence.domain.document.graph;

import static org.assertj.core.api.Assertions.*;

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

		Document createdDocument = Document.createDocument("제목1");
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
		Document parentDocument = Document.createDocument("상위 문서 제목");
		documentContentRepository.save(parentDocument);
		documentGraphService.createDocumentNode(parentDocument);

		// 하위 문서 생성
		Document createdDocument = Document.createDocument("하위 문서 제목");
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