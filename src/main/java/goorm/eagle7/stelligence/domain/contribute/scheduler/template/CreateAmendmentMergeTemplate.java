package goorm.eagle7.stelligence.domain.contribute.scheduler.template;

import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.common.sequence.SectionIdGenerator;
import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.section.SectionRepository;
import goorm.eagle7.stelligence.domain.section.model.Section;
import lombok.extern.slf4j.Slf4j;

/**
 * 생성 타입의 수정안에 대해 병합을 수행합니다.
 */
@Slf4j
@Component
public class CreateAmendmentMergeTemplate extends AmendmentMergeTemplate {

	private final SectionIdGenerator sectionIdGenerator;

	/**
	 * 생성자
	 * 자동 의존 주입 대상입니다.
	 * @param sectionRepository 섹션의 저장소입니다.
	 * @param sectionIdGenerator 새로운 ID를 가진 섹션의 생성을 위해 SectionIdGenerator를 주입받습니다.
	 */
	public CreateAmendmentMergeTemplate(
		SectionRepository sectionRepository,
		SectionIdGenerator sectionIdGenerator
	) {
		super(sectionRepository);
		this.sectionIdGenerator = sectionIdGenerator;
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
		log.trace("새로운 섹션을 생성합니다.");
		return Section.createSection(
			document,
			sectionIdGenerator.getAndIncrementSectionId(), //새로운 섹션의 삽입이므로 ID를 새로 생성합니다.
			document.getCurrentRevision() + 1,
			amendment.getNewSectionHeading(),
			amendment.getNewSectionTitle(),
			amendment.getNewSectionContent(),
			amendment.getTargetSection().getOrder() + amendment.getCreatingOrder()
			//새로운 섹션의 순서는 기존 섹션의 순서 + creatingOrder입니다.
		);
	}

	/**
	 * 새로운 섹션의 생성에 따라 섹션의 순서를 업데이트합니다.
	 * @param section 새롭게 생성된 섹션
	 */
	@Override
	void afterMerged(Section section) {
		log.trace("새로운 섹션의 생성에 따라 섹션의 순서를 업데이트합니다.");
		Document document = section.getDocument();
		sectionRepository.findByVersionWhereOrderGreaterEqualThan(document, document.getCurrentRevision(),
				section.getOrder())
			.forEach(Section::incrementOrder);
	}

}
