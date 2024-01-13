package goorm.eagle7.stelligence.domain.graph;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

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

		final long documentId = 6L;
		final String title = "제목1";

		DocumentNode documentNode = new DocumentNode(documentId, title);
		documentNodeRepository.save(documentNode);
		log.info("documentNode.getDocumentId = {}", documentNode.getDocumentId());
		log.info("documentNode.getTitle = {}", documentNode.getTitle());

		DocumentNode findDocumentNode = documentNodeRepository.findById(documentId).orElseThrow();
		assertThat(findDocumentNode.getDocumentId()).isEqualTo(documentId);
	}

	@Test
	@DisplayName("문서 노드 단일 조회 테스트")
	void findDocumentNode() {

		final long documentId = 1L;
		final String title = "제목1";

		final long wrongDocumentId = 2L;
		final String wrongTitle = "다른 노드 제목2";

		DocumentNode documentNode = new DocumentNode(documentId, title);
		documentNodeRepository.save(documentNode);
		DocumentNode wrongDocumentNode = new DocumentNode(wrongDocumentId, wrongTitle);
		documentNodeRepository.save(wrongDocumentNode);

		DocumentNode findNode = documentNodeRepository.findById(documentId).orElseThrow();

		assertThat(findNode.getDocumentId()).isEqualTo(documentNode.getDocumentId());
		assertThat(findNode.getDocumentId()).isNotEqualTo(wrongDocumentNode.getDocumentId());
	}

	@Test
	@DisplayName("문서 노드 조회 실패 테스트")
	void findDocumentNodeFail() {

		final long nonExistDocumentId = 1L;

		Optional<DocumentNode> findNodeOptional = documentNodeRepository.findById(nonExistDocumentId);

		assertThat(findNodeOptional).isEmpty();
	}

	@Test
	@DisplayName("문서 노드 여러 개 저장 테스트")
	void saveDocumentNodes() {

		final long parentDocumentId = 1L;
		final String parentTitle = "제목1";

		final long childDocumentId = 2L;
		final String childTitle = "제목2";

		// 기존에 있었던 DocumentNode인 parentNode
		DocumentNode parentNode = new DocumentNode(parentDocumentId, parentTitle);
		documentNodeRepository.save(parentNode);
		log.info("parentNode.getDocumentId = {}", parentNode.getDocumentId());
		log.info("parentNode.getTitle = {}", parentNode.getTitle());

		// 새로운 DocumentNode인 childNode
		DocumentNode childNode = new DocumentNode(childDocumentId, childTitle, parentNode);
		documentNodeRepository.save(childNode);
		log.info("childNode.getDocumentId = {}", childNode.getDocumentId());
		log.info("childNode.getTitle = {}", childNode.getTitle());

		// 저장된 자식 노드 조회
		DocumentNode findDocumentNode = documentNodeRepository.findById(childDocumentId).orElseThrow();
		assertThat(findDocumentNode.getDocumentId()).isEqualTo(childDocumentId);
		assertThat(findDocumentNode.getTitle()).isEqualTo(childTitle);
		assertThat(findDocumentNode.getGroup()).isEqualTo(parentNode.getGroup());
		assertThat(findDocumentNode.getParentDocumentNode().getDocumentId()).isEqualTo(parentDocumentId);
	}
}