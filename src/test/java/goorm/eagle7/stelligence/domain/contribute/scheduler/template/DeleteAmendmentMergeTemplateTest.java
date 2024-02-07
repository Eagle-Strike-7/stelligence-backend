package goorm.eagle7.stelligence.domain.contribute.scheduler.template;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.amendment.model.AmendmentType;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.section.model.Heading;
import goorm.eagle7.stelligence.domain.section.model.Section;

@ExtendWith(MockitoExtension.class)
class DeleteAmendmentMergeTemplateTest {

	@Test
	void createSection() {
		//given
		DeleteAmendmentMergeTemplate deleteAmendmentMergeTemplate = new DeleteAmendmentMergeTemplate(null);

		Document document = document(1L, null, "title", 1L);
		Section section = section(4L, 1L, document, Heading.H1, "title", "content", 1);
		Amendment amendment = amendment(1L, null, AmendmentType.DELETE, section, null, null, null, 0);

		//when
		Section createdSection = deleteAmendmentMergeTemplate.createSection(document, amendment);

		//then
		assertThat(createdSection.getId()).isEqualTo(section.getId());
		assertThat(createdSection.getDocument()).isEqualTo(document);
		assertThat(createdSection.getRevision()).isEqualTo(document.getLatestRevision() + 1);
		assertThat(createdSection.getHeading()).isNull();
		assertThat(createdSection.getTitle()).isNull();
		assertThat(createdSection.getContent()).isNull();
		assertThat(createdSection.getOrder()).isEqualTo(section.getOrder());
	}
}