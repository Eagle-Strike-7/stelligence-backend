package goorm.eagle7.stelligence.domain.contribute.scheduler.template;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.common.sequence.SectionIdGenerator;
import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.amendment.model.AmendmentType;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.section.SectionRepository;
import goorm.eagle7.stelligence.domain.section.model.Heading;
import goorm.eagle7.stelligence.domain.section.model.Section;

@ExtendWith(MockitoExtension.class)
class CreateAmendmentMergeTemplateTest {

	@Mock
	SectionRepository sectionRepository;

	@Mock
	SectionIdGenerator sectionIdGenerator;

	@InjectMocks
	CreateAmendmentMergeTemplate createAmendmentMergeTemplate;

	@Test
	void createSection() {
		//given
		Document document = document(1L, null, "title", 1L);
		Section section = section(1L, 1L, document, Heading.H1, "title", "content", 1);
		Amendment amendment = amendment(1L, null, AmendmentType.CREATE, section, Heading.H1, "newTitle", "newContent",
			1);

		//when
		when(sectionIdGenerator.getAndIncrementSectionId()).thenReturn(1L);
		Section createdSection = createAmendmentMergeTemplate.createSection(document, amendment);

		//then
		verify(sectionIdGenerator, times(1)).getAndIncrementSectionId();

		assertThat(createdSection.getId()).isEqualTo(1L);
		assertThat(createdSection.getDocument()).isEqualTo(document);

		assertThat(createdSection.getHeading()).isEqualTo(Heading.H1);
		assertThat(createdSection.getTitle()).isEqualTo("newTitle");
		assertThat(createdSection.getContent()).isEqualTo("newContent");

		//새로 생성된 섹션의 revision은 document의 revision + 1이다.
		assertThat(createdSection.getRevision()).isEqualTo(document.getCurrentRevision() + 1);

		//createdSection의 order는 targetSection의 order + 1이다.
		assertThat(createdSection.getOrder()).isEqualTo(section.getOrder() + 1);
	}

	@Test
	void afterMerged() {
		//given
		Document document = document(1L, null, "title", 3L);
		Section section = section(1L, 1L, document, Heading.H1, "title", "content", 4);

		//when
		createAmendmentMergeTemplate.afterMerged(section);

		//then
		//updateOrders가 호출되었는지 확인한다.
		verify(sectionRepository, times(1)).updateOrders(
			document.getId(),
			document.getCurrentRevision(),
			section.getOrder()
		);
	}
}