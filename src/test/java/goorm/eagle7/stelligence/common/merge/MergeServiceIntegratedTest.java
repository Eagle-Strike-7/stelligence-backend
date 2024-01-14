package goorm.eagle7.stelligence.common.merge;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.common.sequence.SectionIdGenerator;
import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.amendment.model.AmendmentType;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.document.model.Document;
import goorm.eagle7.stelligence.domain.section.model.Heading;
import goorm.eagle7.stelligence.domain.section.model.Section;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest
@Transactional
class MergeServiceIntegratedTest {

	@Autowired
	MergeService mergeService;

	@PersistenceContext
	EntityManager em;

	@Autowired
	SectionIdGenerator sectionIdGenerator;

	@Test
	void merge() {
		//given
		Document document = Document.createDocument("title");

		Section section1 = Section.createSection(
			document,
			sectionIdGenerator.getAndIncrementSectionId(),
			1L,
			Heading.H1,
			"title",
			"content",
			1
		);

		Section section2 = Section.createSection(
			document,
			sectionIdGenerator.getAndIncrementSectionId(),
			1L,
			Heading.H2,
			"title2",
			"content2",
			2
		);

		Contribute contribute = Contribute.createContribute();

		Amendment amendment1 = new Amendment(
			null,
			"amendmentTitle",
			"amendmentDescription",
			AmendmentType.CREATE,
			section1,
			Heading.H2,
			"newTitle",
			"newContent"
		);

		amendment1.setContribute(contribute);

		Amendment amendment2 = new Amendment(
			null,
			"amendmentTitle",
			"amendmentDescription",
			AmendmentType.UPDATE,
			section2,
			Heading.H2,
			"updateTitle",
			"updateContent"
		);

		amendment2.setContribute(contribute);

		em.persist(document);
		em.persist(section1);
		em.persist(section2);
		em.persist(contribute);
		em.persist(amendment1);
		em.persist(amendment2);

		em.flush();
		em.clear();

		//when

		Document findDocument = em.find(Document.class, document.getId());
		Contribute findContribute = em.find(Contribute.class, contribute.getId());

		mergeService.merge(findDocument.getId(), findContribute);

		em.flush();
		em.clear();
		//then

		Document mergedDocument = em.find(Document.class, document.getId());

		assertThat(mergedDocument.getCurrentRevision()).isEqualTo(2L);
		assertThat(mergedDocument.getSections()).hasSize(4);

		//Merge 대상 섹션은 새로 생성되어야 하며, revision이 증가해야 한다.
		Section updatedSection = mergedDocument.getSections().get(2);
		Section insertedSection = mergedDocument.getSections().get(3);
		assertThat(updatedSection.getRevision()).isEqualTo(2);
		assertThat(updatedSection.getId()).isEqualTo(section2.getId());
		assertThat(insertedSection.getRevision()).isEqualTo(2);
		assertThat(insertedSection.getId()).isEqualTo(3L);

	}
}