package goorm.eagle7.stelligence.domain.amendment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentRequest;
import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.section.SectionRepository;
import goorm.eagle7.stelligence.domain.section.model.Section;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AmendmentService {

	private final SectionRepository sectionRepository;

	/**
	 * 타입 구분
	 */
	@Transactional
	public Amendment processAmendment(AmendmentRequest amendmentRequest) {
		return switch (amendmentRequest.getType()) {
			case CREATE -> createAmendment(amendmentRequest);
			case UPDATE -> updateAmendment(amendmentRequest);
			case DELETE -> deleteAmendment(amendmentRequest);
		};
	}

	/**
	 * 수정안 생성(새로운 문단 생성)
	 */
	@Transactional
	public Amendment createAmendment(AmendmentRequest amendmentRequest) {
		Section section = sectionRepository.findLatestSection(amendmentRequest.getSectionId());

		return Amendment.forCreate(
			section,
			amendmentRequest.getNewSectionHeading(),
			amendmentRequest.getNewSectionTitle(),
			amendmentRequest.getNewSectionContent(),
			amendmentRequest.getCreatingOrder()
		);
	}

	/**
	 * 수정안 생성(기존 문단 수정)
	 */
	@Transactional
	public Amendment updateAmendment(AmendmentRequest amendmentRequest) {
		Section section = sectionRepository.findLatestSection(amendmentRequest.getSectionId());

		return Amendment.forUpdate(
			section,
			amendmentRequest.getNewSectionHeading(),
			amendmentRequest.getNewSectionTitle(),
			amendmentRequest.getNewSectionContent()
		);
	}

	/**
	 * 수정안 생성(기존 문단 삭제)
	 */
	@Transactional
	public Amendment deleteAmendment(AmendmentRequest amendmentRequest) {
		Section section = sectionRepository.findLatestSection(amendmentRequest.getSectionId());

		return Amendment.forDelete(section);
	}

}
