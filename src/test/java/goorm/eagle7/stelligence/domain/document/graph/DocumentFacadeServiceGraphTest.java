package goorm.eagle7.stelligence.domain.document.graph;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.config.TestConfig;
import goorm.eagle7.stelligence.domain.document.DocumentService;
import goorm.eagle7.stelligence.domain.document.content.dto.DocumentResponse;
import goorm.eagle7.stelligence.domain.document.dto.DocumentCreateRequest;
import goorm.eagle7.stelligence.domain.document.graph.model.DocumentNode;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Transactional
@Slf4j
@Import(TestConfig.class)
class DocumentFacadeServiceGraphTest {

	@Autowired
	DocumentService documentService;
	@Autowired
	DocumentNodeRepository documentNodeRepository;

	@Test
	@DisplayName("DocumentContent를 생성할 때 DocumentNode도 함께 생성")
	void createDocument() {
		String title = "title";
		String rawContent =
			"# title1\n"
				+ "content1\n"
				+ "## title2\n"
				+ "content2 line 1\n"
				+ "content2 line 2\n"
				+ "### title3\n"
				+ "content3";

		DocumentCreateRequest documentCreateRequest = DocumentCreateRequest.of(title, null, rawContent);
		DocumentResponse documentResponse = documentService.createDocument(documentCreateRequest, 1L);

		DocumentNode documentNode = documentNodeRepository.findById(documentResponse.getDocumentId()).get();

		assertThat(documentNode.getDocumentId()).isEqualTo(documentResponse.getDocumentId());
		assertThat(documentNode.getTitle()).isEqualTo(documentCreateRequest.getTitle());
	}

	@Test
	@DisplayName("DocumentContent를 생성할 때 DocumentNode의 상위 문서를 지정 가능")
	void createDocumentWithParentId() {

		//given
		String parentTitle = "parent";
		String childTitle = "child";
		String rawContent =
			"# title1\n"
				+ "content1\n"
				+ "## title2\n"
				+ "content2 line 1\n"
				+ "content2 line 2\n"
				+ "### title3\n"
				+ "content3";

		DocumentCreateRequest documentCreateRequest1 = DocumentCreateRequest.of(parentTitle, null, rawContent);
		DocumentResponse documentResponse = documentService.createDocument(documentCreateRequest1, 1L);
		Long parentNodeId = documentResponse.getDocumentId();

		//when
		DocumentCreateRequest documentCreateRequest2 = DocumentCreateRequest.of(childTitle, parentNodeId, rawContent);
		DocumentResponse documentResponse2 = documentService.createDocument(documentCreateRequest2, 1L);

		//then
		DocumentNode childDocumentNode = documentNodeRepository.findById(documentResponse2.getDocumentId()).get();
		assertThat(childDocumentNode.getParentDocumentNode().getDocumentId()).isEqualTo(parentNodeId);
		assertThat(childDocumentNode.getTitle()).isEqualTo(childTitle);
		assertThat(childDocumentNode.getGroup()).isEqualTo(parentTitle);
	}
}
