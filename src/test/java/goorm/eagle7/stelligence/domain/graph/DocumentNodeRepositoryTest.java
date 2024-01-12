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

		DocumentNode documentNode = new DocumentNode("제목1", 1L, "제목1");
		documentNodeRepository.save(documentNode);
		log.info("documentNode.getId = {}", documentNode.getId());

		assertThat(documentNode.getId()).isNotNull();
	}

	@Test
	@DisplayName("문서 노드 단일 조회 테스트")
	void findDocumentNode() {

		DocumentNode documentNode = new DocumentNode("제목1", 1L, "제목1");
		documentNodeRepository.save(documentNode);

		DocumentNode findNode = documentNodeRepository.findById(documentNode.getId()).orElseThrow();

		assertThat(documentNode.getId()).isEqualTo(findNode.getId());
		assertThat(documentNode.getTitle()).isEqualTo(findNode.getTitle());
	}
}