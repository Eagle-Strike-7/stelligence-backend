package goorm.eagle7.stelligence.common.merge.handler;

import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.section.SectionRepository;
import goorm.eagle7.stelligence.domain.section.model.Section;

/**
 * UpdateAmendmentMergeTemplate
 * 수정 타입의 수정안에 대해 병합을 수행합니다.
 *
 */
@Component
public class UpdateAmendmentMergeTemplate extends AmendmentMergeTemplate {

	public UpdateAmendmentMergeTemplate(SectionRepository sectionRepository) {
		super(sectionRepository);
	}

	/**
	 * 수정안의 정보를 바탕으로 새로운 섹션을 생성합니다.
	 *
	 * 수정 타입의 경우, 기존 섹션의 ID를 그대로 사용합니다.
	 * 기존 섹션 ID와 함께 변경된 내용을 갖는 새로운 섹션을 삽입하는 것으로 완료됩니다.
	 * 순서는 기존의 순서를 따라야합니다.
	 * @param document
	 * @param amendment
	 * @return
	 */
	@Override
	Section createSection(Document document, Amendment amendment) {
		return Section.createSection(
			document,
			amendment.getTargetSection().getId(), //기존 섹션의 ID를 그대로 사용합니다.
			document.getCurrentRevision() + 1,
			amendment.getNewSectionHeading(),
			amendment.getNewSectionTitle(),
			amendment.getNewSectionContent(),
			amendment.getTargetSection().getOrder() // 기존 섹션의 순서를 따릅니다.
		);
	}

	@Override
	void afterMerged(Section section) {
		//do nothing
	}
}