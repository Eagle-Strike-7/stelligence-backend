package goorm.eagle7.stelligence.domain.contribute.scheduler.template;

import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.section.SectionRepository;
import goorm.eagle7.stelligence.domain.section.model.Section;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AmendmentMergeTemplate
 * 서로 다른 타입에 대하여 다르게 동작하는 병합과정을 분리해내고 공통적인 부분을 추출해낸 추상클래스입니다.
 * Template Method Pattern을 사용하여, 각 타입에 맞는 섹션의 생성은 createSection에서 수행하고
 * sectionRepository.save 메서드나 member.contributes 증가시키는 로직과 같이
 * 공통적으로 수행되어야 하는 코드는 이곳에서 수행됩니다.
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AmendmentMergeTemplate {

	protected final SectionRepository sectionRepository;

	/**
	 * Amendment Type에 따라 서로 다른 방식의 Section을 생성합니다.
	 * 이렇게 생성된 Section은 SectionRepository를 통해 저장됩니다.
	 *
	 * @param document 섹션이 생성될 Document
	 * @param amendment 섹션을 생성하기 위한 정보를 담고 있는 Amendment
	 * @return 새로 생성된 섹션으로 DB에 저장되지 않은 상태입니다.
	 */
	abstract Section createSection(Document document, Amendment amendment);

	/**
	 * Amendment Type에 따라 서로 다른 방식의 추가 작업을 수행합니다.
	 * @param section 병합된 섹션
	 */
	abstract void afterMerged(Section section);

	public final void handle(Document document, Amendment amendment) {
		//템플릿에 따라 Section을 생성한다.
		log.trace("템플릿에 따라 Section을 생성합니다.");
		Section section = createSection(document, amendment);

		//템플릿에 상관없이 공통적으로 섹션을 저장한다.
		log.trace("생성한 섹션을 저장합니다.");
		sectionRepository.save(section);

		//템플릿에 따라 추가적인 작업을 수행한다.
		log.trace("템플릿에 따라 추가적인 작업을 수행합니다.");
		afterMerged(section);
	}
}
