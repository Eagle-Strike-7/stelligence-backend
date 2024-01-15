package goorm.eagle7.stelligence.domain.document;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.config.TestConfig;
import goorm.eagle7.stelligence.config.mockdata.WithMockData;
import goorm.eagle7.stelligence.domain.document.dto.DocumentResponse;
import goorm.eagle7.stelligence.domain.document.dto.SectionResponse;

@SpringBootTest
@Transactional
@WithMockData
@Import(TestConfig.class)
class DocumentServiceReadTest {

	@Autowired
	private DocumentService documentService;

	@Test
	@DisplayName("문서 조회 - 최신버전")
	void getLatestDocumentSuccess() {

		//when
		DocumentResponse document = documentService.getDocument(1L);

		//then
		assertThat(document.getTitle()).isEqualTo("title1");
		assertThat(document.getSections()).hasSize(3);

		SectionResponse section1 = document.getSections().get(0);

		assertThat(section1.getSectionId()).isEqualTo(2L);
		assertThat(section1.getRevision()).isEqualTo(2L);
		assertThat(section1.getTitle()).isEqualTo("document1_title2_update");

		SectionResponse section2 = document.getSections().get(1);

		assertThat(section2.getSectionId()).isEqualTo(3L);
		assertThat(section2.getRevision()).isEqualTo(1L);
		assertThat(section2.getTitle()).isEqualTo("document1_title3");

		SectionResponse section3 = document.getSections().get(2);

		assertThat(section3.getSectionId()).isEqualTo(13L);
		assertThat(section3.getRevision()).isEqualTo(2L);
		assertThat(section3.getTitle()).isEqualTo("document1_title4_insert");

	}

	@Test
	@DisplayName("문서 조회 - 구버전")
	void getDocumentByVersionSuccess() {

		//when
		DocumentResponse document1 = documentService.getDocument(1L, 1L);
		DocumentResponse document2 = documentService.getDocument(1L, 2L);

		//then
		//Document1
		List<SectionResponse> sections1 = document1.getSections();
		assertThat(sections1).hasSize(3);

		assertThat(sections1.get(0).getSectionId()).isEqualTo(1L);
		assertThat(sections1.get(0).getRevision()).isEqualTo(1L);
		assertThat(sections1.get(0).getTitle()).isEqualTo("document1_title1");

		assertThat(sections1.get(1).getSectionId()).isEqualTo(2L);
		assertThat(sections1.get(1).getRevision()).isEqualTo(1L);
		assertThat(sections1.get(1).getTitle()).isEqualTo("document1_title2");

		assertThat(sections1.get(2).getSectionId()).isEqualTo(3L);
		assertThat(sections1.get(2).getRevision()).isEqualTo(1L);
		assertThat(sections1.get(2).getTitle()).isEqualTo("document1_title3");

		//Document2
		List<SectionResponse> sections2 = document2.getSections();
		assertThat(sections2).hasSize(4);

		assertThat(sections2.get(0).getSectionId()).isEqualTo(1L);
		assertThat(sections2.get(0).getRevision()).isEqualTo(1L);
		assertThat(sections2.get(0).getTitle()).isEqualTo("document1_title1");

		assertThat(sections2.get(1).getSectionId()).isEqualTo(2L);
		assertThat(sections2.get(1).getRevision()).isEqualTo(2L);
		assertThat(sections2.get(1).getTitle()).isEqualTo("document1_title2_update");

		assertThat(sections2.get(2).getSectionId()).isEqualTo(3L);
		assertThat(sections2.get(2).getRevision()).isEqualTo(1L);
		assertThat(sections2.get(2).getTitle()).isEqualTo("document1_title3");

		assertThat(sections2.get(3).getSectionId()).isEqualTo(13L);
		assertThat(sections2.get(3).getRevision()).isEqualTo(2L);
		assertThat(sections2.get(3).getTitle()).isEqualTo("document1_title4_insert");

	}
}
