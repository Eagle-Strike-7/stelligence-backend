package goorm.eagle7.stelligence.domain.contribute.scheduler.template;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.amendment.model.AmendmentType;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.section.model.Heading;
import goorm.eagle7.stelligence.domain.section.model.Section;

class UpdateAmendmentMergeTemplateTest {

	@Test
	void createSection() {
		//given
		UpdateAmendmentMergeTemplate updateAmendmentMergeTemplate = new UpdateAmendmentMergeTemplate(null);

		Document document = document(1L, null, "title", 1L);
		Section section = section(1L, 1L, document, Heading.H1, "title", "content", 1);
		Amendment amendment = amendment(1L, null, AmendmentType.UPDATE, section, Heading.H1, "newTitle", "newContent",
			1);

		//when
		Section createdSection = updateAmendmentMergeTemplate.createSection(document, amendment);

		//then
		assertThat(createdSection.getId()).isEqualTo(section.getId());
		assertThat(createdSection.getRevision()).isEqualTo(document.getLatestRevision() + 1);
		assertThat(createdSection.getDocument()).isEqualTo(document);
		assertThat(createdSection.getHeading()).isEqualTo(Heading.H1);
		assertThat(createdSection.getTitle()).isEqualTo("newTitle");
		assertThat(createdSection.getContent()).isEqualTo("newContent");
		assertThat(createdSection.getOrder()).isEqualTo(section.getOrder());
	}
}