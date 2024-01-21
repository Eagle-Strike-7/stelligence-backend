package goorm.eagle7.stelligence.common.merge.template;

import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.section.SectionRepository;
import goorm.eagle7.stelligence.domain.section.model.Section;

/**
 * DeleteAmendmentMergeTemplate
 * 삭제 타입의 수정안에 대해 병합을 수행합니다.
 */
@Component
public class DeleteAmendmentMergeTemplate extends AmendmentMergeTemplate {

	public DeleteAmendmentMergeTemplate(SectionRepository sectionRepository) {
		super(sectionRepository);
	}

	/**
	 * 수정안의 정보를 바탕으로 새로운 섹션을 생성합니다.
	 *
	 * 삭제 타입의 경우, 기존 섹션의 ID를 그대로 사용합니다.
	 * 섹션의 content가 null인 경우 삭제로 인식합니다.
	 *
	 * @param document
	 * @param amendment
	 * @return
	 */
	@Override
	Section createSection(Document document, Amendment amendment) {
		return Section.createSection(
			document,
			amendment.getTargetSection().getId(),
			document.getCurrentRevision() + 1,
			null,
			null,
			null,
			amendment.getTargetSection().getOrder()
		);
	}

	@Override
	void afterMerged(Section section) {
		//do nothing
	}
}
