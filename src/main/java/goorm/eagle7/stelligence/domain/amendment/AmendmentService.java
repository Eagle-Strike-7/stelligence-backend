package goorm.eagle7.stelligence.domain.amendment;

import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.api.exception.BaseException;
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
	private final PolicyFactory policyFactory;

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
	private Amendment createAmendment(AmendmentRequest amendmentRequest) {
		Section section = sectionRepository.findLatestSection(amendmentRequest.getSectionId())
			.orElseThrow(() -> new BaseException("존재하지 않는 섹션입니다. 섹션 ID : " + amendmentRequest.getSectionId()));

		return Amendment.forCreate(
			section,
			amendmentRequest.getNewSectionHeading(),
			amendmentRequest.getNewSectionTitle(),
			policyFactory.sanitize(amendmentRequest.getNewSectionContent()),
			amendmentRequest.getCreatingOrder()
		);
	}

	/**
	 * 수정안 생성(기존 문단 수정)
	 */
	private Amendment updateAmendment(AmendmentRequest amendmentRequest) {
		Section section = sectionRepository.findLatestSection(amendmentRequest.getSectionId())
			.orElseThrow(() -> new BaseException("존재하지 않는 섹션입니다. 섹션 ID : " + amendmentRequest.getSectionId()));

		return Amendment.forUpdate(
			section,
			amendmentRequest.getNewSectionHeading(),
			amendmentRequest.getNewSectionTitle(),
			policyFactory.sanitize(amendmentRequest.getNewSectionContent())
		);
	}

	/**
	 * 수정안 생성(기존 문단 삭제)
	 */
	private Amendment deleteAmendment(AmendmentRequest amendmentRequest) {
		Section section = sectionRepository.findLatestSection(amendmentRequest.getSectionId())
			.orElseThrow(() -> new BaseException("존재하지 않는 섹션입니다. 섹션 ID : " + amendmentRequest.getSectionId()));

		return Amendment.forDelete(section);
	}

}
