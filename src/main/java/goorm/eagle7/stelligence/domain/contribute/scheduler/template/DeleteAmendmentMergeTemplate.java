package goorm.eagle7.stelligence.domain.contribute.scheduler.template;

import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.section.SectionRepository;
import goorm.eagle7.stelligence.domain.section.model.Section;

/**
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
	 * <p>삭제 타입의 경우, 기존 섹션의 ID를 그대로 사용합니다.
	 * 섹션의 heading, title, content는 null로 설정합니다.
	 *
	 * @param document 섹션이 생성될 Document
	 * @param amendment 섹션을 생성하기 위한 정보를 담고 있는 Amendment
	 * @return 새로 생성된 섹션으로 DB에 저장되지 않은 상태입니다.
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
