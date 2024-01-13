package goorm.eagle7.stelligence.domain.graph;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.domain.graph.model.DocumentNode;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Transactional
@Slf4j
class DocumentNodeRepositoryTest {

	@Autowired
	private DocumentNodeRepository documentNodeRepository;

	@Test
	@DisplayName("문서 노드 단일 저장 테스트")
	void saveDocumentNode() {

		final String title = "제목1";
		final long documentId = 1L;

		DocumentNode documentNode = new DocumentNode(title, documentId);
		documentNodeRepository.save(documentNode);
		log.info("documentNode.getNodeId = {}", documentNode.getNodeId());

		assertThat(documentNode.getNodeId()).isNotNull();
	}

	@Test
	@DisplayName("문서 노드 단일 조회 테스트")
	void findDocumentNode() {

		final String title = "제목1";
		final long documentId = 1L;

		DocumentNode documentNode = new DocumentNode(title, documentId);
		documentNodeRepository.save(documentNode);

		DocumentNode findNode = documentNodeRepository.findById(documentNode.getNodeId()).orElseThrow();

		assertThat(documentNode.getNodeId()).isEqualTo(findNode.getNodeId());
		assertThat(documentNode.getTitle()).isEqualTo(findNode.getTitle());
	}

	@Test
	@DisplayName("문서 노드 여러 개 저장 테스트")
	void saveDocumentNodes() {

		final String parentTitle = "제목1";
		final long parentDocumentId = 1L;

		final String childTitle = "제목2";
		final long childDocumentId = 2L;

		// 기존에 있었던 DocumentNode인 parentNode
		DocumentNode parentNode = new DocumentNode(parentTitle, parentDocumentId);
		documentNodeRepository.save(parentNode);
		log.info("parentNode.getNodeId = {}", parentNode.getNodeId());

		// 새로운 DocumentNode인 childNode
		DocumentNode childNode = new DocumentNode(childTitle, childDocumentId, parentNode);
		documentNodeRepository.save(childNode);
		log.info("childNode.getNodeId = {}", childNode.getNodeId());

		// 저장된 자식 노드 조회
		DocumentNode findDocumentNode = documentNodeRepository.findById(childNode.getNodeId()).orElseThrow();
		assertThat(findDocumentNode.getNodeId()).isEqualTo(childNode.getNodeId());
		assertThat(findDocumentNode.getTitle()).isEqualTo(childTitle);
		assertThat(findDocumentNode.getDocumentId()).isEqualTo(childDocumentId);
		assertThat(findDocumentNode.getGroup()).isEqualTo(parentNode.getGroup());
	}
}