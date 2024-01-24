package goorm.eagle7.stelligence.domain.contribute.scheduler.template;

import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.common.sequence.SectionIdGenerator;
import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.section.SectionRepository;
import goorm.eagle7.stelligence.domain.section.model.Section;

/**
 * CreateAmendmentMergeTemplate
 * 생성 타입의 수정안에 대해 병합을 수행합니다.
 */
@Component
public class CreateAmendmentMergeTemplate extends AmendmentMergeTemplate {

	/**
	 * 새로운 ID를 가진 섹션의 생성을 위해 SectionIdGenerator를 주입받습니다.
	 */
	private final SectionIdGenerator sectionIdGenerator;
	private final SectionRepository sectionRepository;

	public CreateAmendmentMergeTemplate(
		SectionRepository sectionRepository,
		SectionIdGenerator sectionIdGenerator
	) {
		super(sectionRepository);
		this.sectionIdGenerator = sectionIdGenerator;
		this.sectionRepository = sectionRepository;
	}

	/**
	 * 수정안의 정보를 바탕으로 새로운 섹션을 생성합니다.
	 *
	 * @param document 섹션이 생성될 Document
	 * @param amendment 섹션을 생성하기 위한 정보를 담고 있는 Amendment
	 * @return 새로 생성된 섹션으로 DB에 저장되지 않은 상태입니다.
	 */
	@Override
	Section createSection(Document document, Amendment amendment) {
		return Section.createSection(
			document,
			sectionIdGenerator.getAndIncrementSectionId(), //새로운 섹션의 삽입이므로 ID를 새로 생성합니다.
			document.getCurrentRevision() + 1,
			amendment.getNewSectionHeading(),
			amendment.getNewSectionTitle(),
			amendment.getNewSectionContent(),
			amendment.getTargetSection().getOrder() + 1 //섹션은 targetSection의 다음 위치에 삽입됩니다.
		);
	}

	/**
	 * 새로운 섹션의 생성에 따라 섹션의 순서를 업데이트합니다.
	 * @param section
	 */
	@Override
	void afterMerged(Section section) {
		Document document = section.getDocument();
		sectionRepository.updateOrders(document.getId(), document.getCurrentRevision(), section.getOrder());
	}

}
