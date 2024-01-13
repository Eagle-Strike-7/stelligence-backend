package goorm.eagle7.stelligence.domain.document;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.common.sequence.SectionIdGenerator;
import goorm.eagle7.stelligence.domain.MockSectionIdGenerator;
import goorm.eagle7.stelligence.domain.TestConfig;
import goorm.eagle7.stelligence.domain.document.dto.DocumentResponse;
import goorm.eagle7.stelligence.domain.document.dto.SectionRequest;
import goorm.eagle7.stelligence.domain.document.dto.SectionResponse;
import goorm.eagle7.stelligence.domain.document.model.Document;
import goorm.eagle7.stelligence.domain.section.model.Heading;
import goorm.eagle7.stelligence.domain.section.model.Section;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * DocumentService를 테스트합니다.
 */
@SpringBootTest
@Transactional
@Import(TestConfig.class)
class DocumentServiceSpringBootTest {

	@Autowired
	private DocumentService documentService;

	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	private SectionIdGenerator sectionIdGenerator;

	@PersistenceContext
	private EntityManager em;

	@BeforeEach
	void setUp() {
		((MockSectionIdGenerator)sectionIdGenerator).clear();
	}

	@Test
	@DisplayName("문서 생성 - 성공")
	void createDocumentSuccess() {

		String title = "title";

		List<SectionRequest> sectionRequests = List.of(
			new SectionRequest(Heading.H1, "title1", "content1"),
			new SectionRequest(Heading.H2, "title2", "content2"),
			new SectionRequest(Heading.H3, "title3", "content3")
		);

		Long documentId = documentService.createDocument(title, sectionRequests);

		em.flush();
		em.clear();

		Document createdDocs = documentRepository.findById(documentId).get();

		assertThat(createdDocs.getTitle()).isEqualTo(title);
		assertThat(createdDocs.getCurrentRevision()).isEqualTo(1);
		assertThat(createdDocs.getSections()).hasSize(3);

	}

	@Test
	@DisplayName("수정 요청 Merge - Insert 단건")
	void mergeOneInsertContribute() {
		String title = "title";

		List<SectionRequest> sectionRequests = List.of(
			new SectionRequest(Heading.H1, "title1", "content1"),
			new SectionRequest(Heading.H2, "title2", "content2"),
			new SectionRequest(Heading.H3, "title3", "content3")
		);

		Long documentId = documentService.createDocument(title, sectionRequests);

		documentService.mergeContribute(documentId, List.of(
			new DocumentService.Commit("INSERT", 1L, 1L, Heading.H1, "newTitle", "newContent")
		));

		em.flush();
		em.clear();

		Document document = documentRepository.findById(documentId).get();
		int size = document.getSections().size();
		System.out.println("size = " + size);
		assertThat(size).isEqualTo(4);

		//삽입이 잘 이루어졌는지 확인합니다.
		Section insertedSection = document.getSections().get(3);
		assertThat(insertedSection.getRevision()).isEqualTo(2L);
		assertThat(insertedSection.getHeading()).isEqualTo(Heading.H1);
		assertThat(insertedSection.getTitle()).isEqualTo("newTitle");
		assertThat(insertedSection.getOrder()).isEqualTo(2);

		//순서가 변경되었는지 확인합니다.
		assertThat(document.getSections().get(1).getOrder()).isEqualTo(3);
		assertThat(document.getSections().get(2).getOrder()).isEqualTo(4);

	}

	@Test
	@DisplayName("수정 요청 Merge - UPDATE 단건")
	void mergeOneModifyContribute() {
		String title = "title";

		List<SectionRequest> sectionRequests = List.of(
			new SectionRequest(Heading.H1, "title1", "content1"),
			new SectionRequest(Heading.H2, "title2", "content2"),
			new SectionRequest(Heading.H3, "title3", "content3")
		);

		Long documentId = documentService.createDocument(title, sectionRequests);

		documentService.mergeContribute(documentId, List.of(
			new DocumentService.Commit("UPDATE", 1L, 1L, Heading.H2, "newTitle", "newContent")
		));

		em.flush();
		em.clear();

		Document document = documentRepository.findById(documentId).get();
		int size = document.getSections().size();
		assertThat(size).isEqualTo(4);

		//수정이 잘 이루어졌는지 확인합니다.
		Section updatedSection = document.getSections().get(3);
		assertThat(updatedSection.getId()).isEqualTo(1L);
		assertThat(updatedSection.getRevision()).isEqualTo(2L);
		assertThat(updatedSection.getHeading()).isEqualTo(Heading.H2);
		assertThat(updatedSection.getTitle()).isEqualTo("newTitle");
		assertThat(updatedSection.getOrder()).isEqualTo(1);

		//원본이 잘 유지되어있는지 확인합니다.
		Section originalSection = document.getSections().get(0);
		assertThat(originalSection.getRevision()).isEqualTo(1L);
		assertThat(originalSection.getHeading()).isEqualTo(Heading.H1);
		assertThat(originalSection.getTitle()).isEqualTo("title1");
		assertThat(originalSection.getOrder()).isEqualTo(1);

		//순서가 변경되었는지 확인합니다.
		assertThat(document.getSections().get(1).getOrder()).isEqualTo(2);
		assertThat(document.getSections().get(2).getOrder()).isEqualTo(3);

	}

	@Test
	@DisplayName("수정 요청 Merge - DELETE 단건")
	void mergeOneDeleteContribute() {
		String title = "title";

		List<SectionRequest> sectionRequests = List.of(
			new SectionRequest(Heading.H1, "title1", "content1"),
			new SectionRequest(Heading.H2, "title2", "content2"),
			new SectionRequest(Heading.H3, "title3", "content3")
		);

		Long documentId = documentService.createDocument(title, sectionRequests);

		documentService.mergeContribute(documentId, List.of(
			new DocumentService.Commit("DELETE", 2L, 1L, null, null, null)
		));

		em.flush();
		em.clear();

		Document document = documentRepository.findById(documentId).get();
		int size = document.getSections().size();
		assertThat(size).isEqualTo(4);

		//삭제가 잘 이루어졌는지 확인합니다.
		Section updatedSection = document.getSections().get(3);
		assertThat(updatedSection.getId()).isEqualTo(2L);
		assertThat(updatedSection.getRevision()).isEqualTo(2L);
		assertThat(updatedSection.getContent()).isNull();

		//원본이 잘 유지되어있는지 확인합니다.
		Section originalSection = document.getSections().get(1);
		assertThat(originalSection.getRevision()).isEqualTo(1L);
		assertThat(originalSection.getHeading()).isEqualTo(Heading.H2);
		assertThat(originalSection.getTitle()).isEqualTo("title2");
		assertThat(originalSection.getOrder()).isEqualTo(2);

	}

	@Test
	@DisplayName("Commit 여러개 반영")
	void mergeCommits() {
		String title = "title";

		List<SectionRequest> sectionRequests = List.of(
			new SectionRequest(Heading.H1, "title1", "content1"),
			new SectionRequest(Heading.H2, "title2", "content2"),
			new SectionRequest(Heading.H3, "title3", "content3"),
			new SectionRequest(Heading.H3, "title4", "content4")
		);

		Long documentId = documentService.createDocument(title, sectionRequests);

		documentService.mergeContribute(documentId, List.of(
			new DocumentService.Commit("DELETE", 2L, 1L, null, null, null),
			new DocumentService.Commit("UPDATE", 1L, 1L, Heading.H1, "title1Update", "newContent"),
			new DocumentService.Commit("INSERT", 3L, 1L, Heading.H1, "titleInsert", "newContent")
		));

		em.flush();
		em.clear();

		Document document = documentRepository.findById(documentId).get();
		int size = document.getSections().size();
		assertThat(size).isEqualTo(7);

		//원본이 잘 유지되어있는지 확인합니다.
		Section section0 = document.getSections().get(0);
		assertThat(section0.getRevision()).isEqualTo(1L);
		assertThat(section0.getHeading()).isEqualTo(Heading.H1);
		assertThat(section0.getTitle()).isEqualTo("title1");
		assertThat(section0.getOrder()).isEqualTo(1);

		//원본이 잘 유지되어있는지 확인합니다.
		Section section1 = document.getSections().get(1);
		assertThat(section1.getRevision()).isEqualTo(1L);
		assertThat(section1.getHeading()).isEqualTo(Heading.H2);
		assertThat(section1.getTitle()).isEqualTo("title2");
		assertThat(section1.getOrder()).isEqualTo(2);

		//수정가 잘 이루어졌는지 확인합니다. /1/2
		Section section4 = document.getSections().get(4);
		assertThat(section4.getId()).isEqualTo(1L);
		assertThat(section4.getRevision()).isEqualTo(2L);
		assertThat(section4.getTitle()).isEqualTo("title1Update");
		assertThat(section4.getOrder()).isEqualTo(1);

		//삭제이 잘 이루어졌는지 확인합니다. 2/2
		Section section5 = document.getSections().get(5);
		assertThat(section5.getId()).isEqualTo(2L);
		assertThat(section5.getRevision()).isEqualTo(2L);
		assertThat(section5.getContent()).isNull();

		//삽입이 잘 이루어졌는지 확인합니다.
		Section section6 = document.getSections().get(6);
		assertThat(section6.getId()).isEqualTo(5L);
		assertThat(section6.getRevision()).isEqualTo(2L);
		assertThat(section6.getHeading()).isEqualTo(Heading.H1);
		assertThat(section6.getTitle()).isEqualTo("titleInsert");
		assertThat(section6.getOrder()).isEqualTo(4);

		//순서가 변경되었는지 확인합니다.
		assertThat(document.getSections().get(2).getOrder()).isEqualTo(3);
		assertThat(document.getSections().get(3).getOrder()).isEqualTo(5);

	}

	@Test
	@DisplayName("문서 조회 - 최신버전")
	void getLatestDocumentSuccess() {
		//given
		String title = "title";

		List<SectionRequest> sectionRequests = List.of(
			new SectionRequest(Heading.H1, "title1", "content1"),
			new SectionRequest(Heading.H2, "title2", "content2"),
			new SectionRequest(Heading.H3, "title3", "content3")
		);

		Long documentId = documentService.createDocument(title, sectionRequests);

		documentService.mergeContribute(
			documentId,
			List.of(
				new DocumentService.Commit("INSERT", 1L, 1L, Heading.H1, "newTitle", "newContent")
			)
		);

		documentService.mergeContribute(
			documentId,
			List.of(
				new DocumentService.Commit("UPDATE", 1L, 1L, Heading.H1, "title1Update", "title1Update")
			)
		);

		documentService.mergeContribute(
			documentId,
			List.of(
				new DocumentService.Commit("DELETE", 3L, 1L, null, null, null)
			)
		);

		em.flush();
		em.clear();

		//when
		DocumentResponse document = documentService.getDocument(documentId);

		//then
		assertThat(document.getTitle()).isEqualTo(title);
		assertThat(document.getSections()).hasSize(3);

		SectionResponse section0 = document.getSections().get(0);
		assertThat(section0.getSectionId()).isEqualTo(1L);
		assertThat(section0.getRevision()).isEqualTo(3L);
		assertThat(section0.getTitle()).isEqualTo("title1Update");

		SectionResponse section1 = document.getSections().get(1);
		assertThat(section1.getSectionId()).isEqualTo(4L);
		assertThat(section1.getRevision()).isEqualTo(2L);
		assertThat(section1.getTitle()).isEqualTo("newTitle");

		SectionResponse section2 = document.getSections().get(2);
		assertThat(section2.getSectionId()).isEqualTo(2L);
		assertThat(section2.getRevision()).isEqualTo(1L);
		assertThat(section2.getTitle()).isEqualTo("title2");

	}

	@Test
	@DisplayName("문서 조회 - 구버전")
	void getDocumentByVersionSuccess() {
		//given
		String title = "title";

		List<SectionRequest> sectionRequests = List.of(
			new SectionRequest(Heading.H1, "title1", "content1"),
			new SectionRequest(Heading.H2, "title2", "content2"),
			new SectionRequest(Heading.H3, "title3", "content3")
		);

		Long documentId = documentService.createDocument(title, sectionRequests);

		Long documentId2 = documentService.createDocument(title, sectionRequests);

		documentService.mergeContribute(
			documentId,
			List.of(
				new DocumentService.Commit("INSERT", 1L, 1L, Heading.H1, "newTitle", "newContent")
			)
		);

		documentService.mergeContribute(
			documentId,
			List.of(
				new DocumentService.Commit("UPDATE", 1L, 1L, Heading.H1, "title1Update", "title1Update")
			)
		);

		documentService.mergeContribute(
			documentId,
			List.of(
				new DocumentService.Commit("DELETE", 3L, 1L, null, null, null)
			)
		);

		TestTransaction.flagForCommit();
		TestTransaction.end();

		TestTransaction.start();

		//when
		DocumentResponse document1 = documentService.getDocument(documentId, 1L);
		DocumentResponse document2 = documentService.getDocument(documentId, 2L);
		DocumentResponse document3 = documentService.getDocument(documentId, 3L);

		//then
		//Document1
		List<SectionResponse> sections1 = document1.getSections();
		assertThat(sections1).hasSize(3);

		assertThat(sections1.get(0).getSectionId()).isEqualTo(1L);
		assertThat(sections1.get(0).getRevision()).isEqualTo(1L);
		assertThat(sections1.get(0).getTitle()).isEqualTo("title1");

		assertThat(sections1.get(1).getSectionId()).isEqualTo(2L);
		assertThat(sections1.get(1).getRevision()).isEqualTo(1L);
		assertThat(sections1.get(1).getTitle()).isEqualTo("title2");

		assertThat(sections1.get(2).getSectionId()).isEqualTo(3L);
		assertThat(sections1.get(2).getRevision()).isEqualTo(1L);
		assertThat(sections1.get(2).getTitle()).isEqualTo("title3");

		//Document2
		documentRepository.findById(documentId)
			.get()
			.getSections()
			.stream()
			.map(SectionResponse::of)
			.forEach(System.out::println);

		List<SectionResponse> sections2 = document2.getSections();
		assertThat(sections2).hasSize(4);

		assertThat(sections2.get(0).getSectionId()).isEqualTo(1L);
		assertThat(sections2.get(0).getRevision()).isEqualTo(1L);
		assertThat(sections2.get(0).getTitle()).isEqualTo("title1");

		assertThat(sections2.get(1).getSectionId()).isEqualTo(7L);
		assertThat(sections2.get(1).getRevision()).isEqualTo(2L);
		assertThat(sections2.get(1).getTitle()).isEqualTo("newTitle");

		assertThat(sections2.get(2).getSectionId()).isEqualTo(2L);
		assertThat(sections2.get(2).getRevision()).isEqualTo(1L);
		assertThat(sections2.get(2).getTitle()).isEqualTo("title2");

		assertThat(sections2.get(3).getSectionId()).isEqualTo(3L);
		assertThat(sections2.get(3).getRevision()).isEqualTo(1L);
		assertThat(sections2.get(3).getTitle()).isEqualTo("title3");

		//Document3
		List<SectionResponse> sections3 = document3.getSections();

		assertThat(sections3).hasSize(4);

		assertThat(sections3.get(0).getSectionId()).isEqualTo(1L);
		assertThat(sections3.get(0).getRevision()).isEqualTo(3L);
		assertThat(sections3.get(0).getTitle()).isEqualTo("title1Update");

		assertThat(sections3.get(1).getSectionId()).isEqualTo(7L);
		assertThat(sections3.get(1).getRevision()).isEqualTo(2L);
		assertThat(sections3.get(1).getTitle()).isEqualTo("newTitle");

		assertThat(sections3.get(2).getSectionId()).isEqualTo(2L);
		assertThat(sections3.get(2).getRevision()).isEqualTo(1L);
		assertThat(sections3.get(2).getTitle()).isEqualTo("title2");

		assertThat(sections3.get(3).getSectionId()).isEqualTo(3L);
		assertThat(sections3.get(3).getRevision()).isEqualTo(1L);
		assertThat(sections3.get(3).getTitle()).isEqualTo("title3");
	}

}