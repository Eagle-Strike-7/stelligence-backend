package goorm.eagle7.stelligence.domain.contribute.scheduler.template;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

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
		Section section = section(1L, 1L, document, Heading.H1, "title", "content", 3);

		Section s1 = section(2L, 1L, document, Heading.H1, "title", "content", 1);
		Section s2 = section(3L, 1L, document, Heading.H2, "title", "content", 2);
		Section s3 = section(4L, 1L, document, Heading.H3, "title", "content", 3);
		Section s4 = section(5L, 1L, document, Heading.H4, "title", "content", 4);

		//when
		when(sectionRepository.findByVersion(document, document.getCurrentRevision()))
			.thenReturn(List.of(s1, s2, s3, s4));

		createAmendmentMergeTemplate.afterMerged(section);

		//then
		//section의 order보다 높은 order를 가진 section들의 order가 1씩 증가했는지 확인
		assertThat(s1.getOrder()).isEqualTo(1);
		assertThat(s2.getOrder()).isEqualTo(2);
		assertThat(s3.getOrder()).isEqualTo(4);
		assertThat(s4.getOrder()).isEqualTo(5);
	}
}