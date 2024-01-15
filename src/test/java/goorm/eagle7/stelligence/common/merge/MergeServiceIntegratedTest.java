package goorm.eagle7.stelligence.common.merge;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.config.mockdata.WithMockData;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.section.model.Section;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest
@Transactional
@WithMockData
class MergeServiceIntegratedTest {

	@Autowired
	MergeService mergeService;

	@PersistenceContext
	EntityManager em;

	@Test
	void merge() {
		Document document = em.find(Document.class, 2L);
		Long beforeRevision = document.getCurrentRevision();
		int beforeSectionSize = document.getSections().size();

		//5번 Contribute는 투표중이며, 4번 섹션의 뒤에 새로운 섹션을 추가하는 수정안과 6번 섹션을 수정하는 수정안을 갖고있다.
		Contribute contribute = em.find(Contribute.class, 5L);

		mergeService.merge(document.getId(), contribute);

		em.flush();
		em.clear();

		//then
		Document mergedDocument = em.find(Document.class, document.getId());

		//Merge 대상 문서의 revision이 증가해야 한다.
		assertThat(mergedDocument.getCurrentRevision()).isEqualTo(beforeRevision + 1);
		assertThat(mergedDocument.getSections()).hasSize(beforeSectionSize + 2);

		//기존거 잘 있는지 확인
		Section section1 = mergedDocument.getSections().get(0);

		assertThat(section1.getId()).isEqualTo(4L);
		assertThat(section1.getRevision()).isEqualTo(1L);

		//수정된 섹션 확인
		Section section5 = mergedDocument.getSections().get(4);

		assertThat(section5.getId()).isEqualTo(6L);
		assertThat(section5.getRevision()).isEqualTo(3L);
		assertThat(section5.getTitle()).isEqualTo("document2_title3_update");

		//삽입된 섹션 확인
		Section section6 = mergedDocument.getSections().get(5);

		assertThat(section6.getId()).isEqualTo(15L);
		assertThat(section6.getRevision()).isEqualTo(3L);
		assertThat(section6.getTitle()).isEqualTo("document2_title4_insert");

	}
}