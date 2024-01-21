package goorm.eagle7.stelligence.common.merge.template;

import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.section.SectionRepository;
import goorm.eagle7.stelligence.domain.section.model.Section;
import lombok.RequiredArgsConstructor;

/**
 * AmendmentMergeTemplate
 * 서로 다른 타입에 대하여 다르게 동작하는 병합과정을 분리해내고 공통적인 부분을 추출해낸 추상클래스입니다.
 * Template Method Pattern을 사용하여, 각 타입에 맞는 섹션의 생성은 createSection에서 수행하고
 * sectionRepository.save 메서드나 member.contributes 증가시키는 로직과 같이
 * 공통적으로 수행되어야 하는 코드는 이곳에서 수행됩니다.
 */
@Component
@RequiredArgsConstructor
public abstract class AmendmentMergeTemplate {

	private final SectionRepository sectionRepository;

	/**
	 * Amendment Type에 따라 서로 다른 방식의 Section을 생성합니다.
	 * 이렇게 생성된 Section은 SectionRepository를 통해 저장됩니다.
	 * @param document
	 * @param amendment
	 * @return
	 */
	abstract Section createSection(Document document, Amendment amendment);

	/**
	 * Amendment Type에 따라 서로 다른 방식의 추가 작업을 수행합니다.
	 * @param section
	 */
	abstract void afterMerged(Section section);

	public final void handle(Document document, Amendment amendment) {
		//템플릿에 따라 Section을 생성한다.
		Section section = createSection(document, amendment);

		//템플릿에 상관없이 공통적으로 섹션을 저장한다.
		sectionRepository.save(section);

		/**
		 * TODO : 이 부분에서 Member의 Contribute를 올려주는 코드 작성
		 */

		//템플릿에 따라 추가적인 작업을 수행한다.
		afterMerged(section);
	}
}
