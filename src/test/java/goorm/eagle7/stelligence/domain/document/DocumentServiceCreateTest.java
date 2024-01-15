package goorm.eagle7.stelligence.domain.document;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.config.TestConfig;
import goorm.eagle7.stelligence.domain.document.model.Document;
import goorm.eagle7.stelligence.domain.section.model.Heading;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * DocumentService를 테스트합니다.
 */
@SpringBootTest
@Transactional
@Import(TestConfig.class)
class DocumentServiceCreateTest {

	@Autowired
	private DocumentService documentService;
	@PersistenceContext
	private EntityManager em;

	@Test
	@DisplayName("문서 생성 - 성공")
	void createDocumentSuccess() {

		String title = "title";
		String rawContent =
			"# title1\n"
				+ "content1\n"
				+ "## title2\n"
				+ "content2 line 1\n"
				+ "content2 line 2\n"
				+ "### title3\n"
				+ "content3";

		Long documentId = documentService.createDocument(title, rawContent);

		em.flush();
		em.clear();

		Document createdDocs = em.find(Document.class, documentId);

		assertThat(createdDocs.getTitle()).isEqualTo(title);
		assertThat(createdDocs.getCurrentRevision()).isEqualTo(1);
		assertThat(createdDocs.getSections()).hasSize(3);

		//section 1의 title이 잘 들어갔는지 확인합니다.
		assertThat(createdDocs.getSections().get(0).getTitle()).isEqualTo("title1");

		//section 2의 content가 잘 들어갔는지 확인합니다.
		assertThat(createdDocs.getSections().get(1).getContent()).isEqualTo("content2 line 1\ncontent2 line 2\n");

		//section 3의 heading이 잘 들어갔는지 확인합니다.
		assertThat(createdDocs.getSections().get(2).getHeading()).isEqualTo(Heading.H3);

	}

}