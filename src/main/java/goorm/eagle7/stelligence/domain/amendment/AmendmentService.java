package goorm.eagle7.stelligence.domain.amendment;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentResponse;
import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentSaveCreateRequest;
import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentSaveDeleteRequest;
import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentSaveUpdateRequest;
import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentUpdateRequest;
import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.amendment.model.AmendmentStatus;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.section.SectionRepository;
import goorm.eagle7.stelligence.domain.section.model.Section;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AmendmentService {

	private final AmendmentRepository amendmentRepository;
	private final SectionRepository sectionRepository;
	private final MemberRepository memberRepository;

	/**
	 * 수정안 생성(새로운 문단 생성)
	 */
	@Transactional
	public AmendmentResponse saveCreateAmendment(AmendmentSaveCreateRequest amendmentSaveCreateRequest, Long memberId) {
		Section section = sectionRepository.findLatestSection(amendmentSaveCreateRequest.getSectionId());

		Member member = memberRepository.findById(memberId).orElseThrow(
			() -> new BaseException("존재하지 않는 회원의 요청입니다. 사용자 ID: " + memberId));

		Amendment amendment = Amendment.forCreate(
			member,
			amendmentSaveCreateRequest.getTitle(),
			amendmentSaveCreateRequest.getDescription(),
			section,
			amendmentSaveCreateRequest.getHeading(),
			amendmentSaveCreateRequest.getSectionTitle(),
			amendmentSaveCreateRequest.getSectionContent()
		);

		amendmentRepository.save(amendment);

		return AmendmentResponse.of(amendment);
	}

	/**
	 * 수정안 생성(기존 문단 수정)
	 */
	@Transactional
	public AmendmentResponse saveUpdateAmendment(AmendmentSaveUpdateRequest amendmentSaveUpdateRequest, Long memberId) {
		Section section = sectionRepository.findLatestSection(amendmentSaveUpdateRequest.getSectionId());

		Member member = memberRepository.findById(memberId).orElseThrow(
			() -> new BaseException("존재하지 않는 회원의 요청입니다. 사용자 ID: " + memberId));

		Amendment amendment = Amendment.forUpdate(
			member,
			amendmentSaveUpdateRequest.getTitle(),
			amendmentSaveUpdateRequest.getDescription(),
			section,
			amendmentSaveUpdateRequest.getHeading(),
			amendmentSaveUpdateRequest.getSectionTitle(),
			amendmentSaveUpdateRequest.getSectionContent()
		);

		amendmentRepository.save(amendment);

		return AmendmentResponse.of(amendment);
	}

	/**
	 * 수정안 생성(기존 문단 삭제)
	 */
	@Transactional
	public AmendmentResponse saveDeleteAmendment(AmendmentSaveDeleteRequest amendmentSaveDeleteRequest, Long memberId) {
		Section section = sectionRepository.findLatestSection(amendmentSaveDeleteRequest.getSectionId());

		Member member = memberRepository.findById(memberId).orElseThrow(
			() -> new BaseException("존재하지 않는 회원의 요청입니다. 사용자 ID: " + memberId));

		Amendment amendment = Amendment.forDelete(member, amendmentSaveDeleteRequest.getTitle(),
			amendmentSaveDeleteRequest.getDescription(), section);

		amendmentRepository.save(amendment);

		return AmendmentResponse.of(amendment);
	}

	/**
	 * 수정안 삭제
	 */
	@Transactional
	public void deleteAmendment(Long amendmentId, Long memberId) {
		Amendment amendment = amendmentRepository.findById(amendmentId).orElseThrow(
			() -> new BaseException("존재하지 않는 수정안입니다. 수정안 ID : " + amendmentId));

		// 삭제 요청한 사용자가 수정안을 삭제할 권한을 가지고 있는지 검즘
		if (!amendment.hasPermissionToDeleteOrUpdate(memberId)) {
			throw new BaseException("삭제 권한이 없습니다. 사용자 ID: " + memberId);
		}

		// 상태가 REQUESTED인 경우 삭제 불가
		if (amendment.isNotDeletable()) {
			throw new BaseException("이미 요청중인 수정안은 삭제할 수 없습니다.");
		}

		amendmentRepository.delete(amendment);
	}

	/**
	 * 수정안 개별 조회
	 */
	public AmendmentResponse getAmendment(Long amendmentId) {
		Amendment amendment = amendmentRepository.findById(amendmentId).orElseThrow(
			() -> new BaseException("존재하지 않는 수정안입니다. 수정안 ID : " + amendmentId));

		return AmendmentResponse.of(amendment);
	}

	/**
	 * 수정안 목록 조회
	 */
	public List<AmendmentResponse> getAmendments(AmendmentStatus status, Long documentId, Long memberId) {

		//파라미터가 null이면 그 파라미터는 목록 조회 조건에 포함이 되지 않음
		List<Amendment> amendments = amendmentRepository.findAmendments(status, documentId, memberId);

		return amendments.stream()
			.map(AmendmentResponse::of)
			.toList();
	}

	/**
	 * 수정안 수정
	 */
	@Transactional
	public AmendmentResponse updateAmendment(AmendmentUpdateRequest amendmentUpdateRequest, Long documentId,
		Long memberId) {
		Amendment amendment = amendmentRepository.findById(documentId).orElseThrow(
			() -> new BaseException("존재하지 않는 수정안입니다. 수정안 ID : " + documentId));

		// 수정 요청한 사용자가 수정안의 작성자와 일치하는지 검증
		if (!amendment.hasPermissionToDeleteOrUpdate(memberId)) {
			throw new BaseException("수정 권한이 없습니다. 사용자 ID: " + memberId);
		}

		//내부에서 status를 검사함
		try {
			Amendment update = amendment.updateContent(amendmentUpdateRequest.getTitle(),
				amendmentUpdateRequest.getDescription(), amendmentUpdateRequest.getHeading(),
				amendmentUpdateRequest.getSectionTitle(), amendmentUpdateRequest.getSectionContent());

			return AmendmentResponse.of(update);
		} catch (IllegalStateException e) {
			throw new BaseException("수정 요청 후에는 수정안을 수정할 수 없습니다.");
		}
	}
}
