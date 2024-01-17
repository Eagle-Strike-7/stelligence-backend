package goorm.eagle7.stelligence.domain.amendment;

import static goorm.eagle7.stelligence.domain.amendment.model.QAmendment.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.amendment.dto.AmendmenCreateSavetRequest;
import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentDeleteSaveRequest;
import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentResponse;
import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentUpdateSaveRequest;
import goorm.eagle7.stelligence.domain.amendment.dto.UpdateAmendmentRequest;
import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.amendment.model.AmendmentStatus;
import goorm.eagle7.stelligence.domain.amendment.model.AmendmentType;
import goorm.eagle7.stelligence.domain.document.content.DocumentContentRepository;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.section.SectionRepository;
import goorm.eagle7.stelligence.domain.section.model.Section;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AmendmentService {
	private final EntityManager em;

	private final AmendmentRepository amendmentRepository;
	private final SectionRepository sectionRepository;
	private final MemberRepository memberRepository;
	private final DocumentContentRepository documentRepository;

	/**
	 * 수정안 생성(새로운 문단 생성)
	 */
	@Transactional
	public AmendmentResponse saveCreateAmendment(AmendmenCreateSavetRequest amendmenCreateSavetRequest, Long memberId) {
		Section section = sectionRepository.findLatestSection(amendmenCreateSavetRequest.getSectionId());

		Member member = memberRepository.findById(memberId).orElseThrow(
			() -> new BaseException("존재하지 않는 id입니다. ID : " + memberId));

		Amendment amendment = new Amendment(
			member,
			amendmenCreateSavetRequest.getTitle(),
			amendmenCreateSavetRequest.getDescription(),
			AmendmentType.CREATE,
			section,
			amendmenCreateSavetRequest.getHeading(),
			amendmenCreateSavetRequest.getSectionTitle(),
			amendmenCreateSavetRequest.getSectionContent()
		);

		amendmentRepository.save(amendment);

		return AmendmentResponse.of(amendment);
	}

	/**
	 * 수정안 생성(기존 문단 수정)
	 */
	@Transactional
	public AmendmentResponse saveUpdateAmendment(AmendmentUpdateSaveRequest amendmentUpdateSaveRequest, Long memberId) {
		Section section = sectionRepository.findLatestSection(amendmentUpdateSaveRequest.getSectionId());

		Member member = memberRepository.findById(memberId).orElseThrow(
			() -> new BaseException("존재하지 않는 id입니다. ID : " + memberId));

		Amendment amendment = new Amendment(
			member,
			amendmentUpdateSaveRequest.getTitle(),
			amendmentUpdateSaveRequest.getDescription(),
			AmendmentType.UPDATE,
			section,
			amendmentUpdateSaveRequest.getHeading(),
			amendmentUpdateSaveRequest.getSectionTitle(),
			amendmentUpdateSaveRequest.getSectionContent()
		);

		amendmentRepository.save(amendment);

		return AmendmentResponse.of(amendment);
	}

	/**
	 * 수정안 생성(기존 문단 삭제)
	 */
	@Transactional
	public AmendmentResponse saveDeleteAmendment(AmendmentDeleteSaveRequest amendmentDeleteSaveRequest, Long memberId) {
		Section section = sectionRepository.findLatestSection(amendmentDeleteSaveRequest.getSectionId());

		Member member = memberRepository.findById(memberId).orElseThrow(
			() -> new BaseException("존재하지 않는 id입니다. ID : " + memberId));

		Amendment amendment = new Amendment(
			member,
			amendmentDeleteSaveRequest.getTitle(),
			amendmentDeleteSaveRequest.getDescription(),
			AmendmentType.DELETE,
			section
		);

		amendmentRepository.save(amendment);

		return AmendmentResponse.of(amendment);
	}

	/**
	 * 수정안 삭제
	 */
	@Transactional
	public void deleteAmendment(Long id, Long memberId) {
		Amendment amendment = amendmentRepository.findById(id).orElseThrow(
			() -> new BaseException("존재하지 않는 수정안입니다. ID : " + id));

		// 삭제 요청한 사용자가 수정안의 작성자와 일치하는지 검증
		if (!amendment.getMember().getId().equals(memberId)) {
			throw new BaseException("수정 권한이 없습니다. 사용자 ID: " + memberId);
		}

		amendmentRepository.deleteById(id);
	}

	/**
	 * 수정안 개별 조회
	 */
	public AmendmentResponse getAmendment(Long id) {
		Amendment amendment = amendmentRepository.findById(id).orElseThrow(
			() -> new BaseException("존재하지 않는 수정안입니다. ID : " + id));

		return AmendmentResponse.of(amendment);
	}

	/**
	 * 수정안 목록 조회
	 */
	public List<AmendmentResponse> getAmendments(Long memberId, Long documentId, AmendmentStatus status) {
		BooleanBuilder builder = new BooleanBuilder();
		JPAQueryFactory query = new JPAQueryFactory(em);

		//파라미터가 null이면 그 파라미터는 목록 조회 조건에 포함이 되지 않음
		if (memberId != null) {
			Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 id입니다. ID: " + memberId));
			builder.and(amendment.member.eq(member));
		}

		if (documentId != null) {
			Document document = documentRepository.findById(documentId)
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 문서입니다 ID: " + documentId));
			builder.and(amendment.targetSection.document.eq(document));
		}

		if (status != null) {
			builder.and(amendment.status.eq(status));
		}

		List<Amendment> amendments = query
			.selectFrom(amendment)
			.where(builder)
			.fetch();

		return amendments.stream()
			.map(AmendmentResponse::of)
			.toList();
	}

	/**
	 * 수정안 수정
	 */
	@Transactional
	public AmendmentResponse updateAmendment(Long id, Long memberId, UpdateAmendmentRequest updateAmendmentRequest) {
		Amendment amendment = amendmentRepository.findById(id).orElseThrow(
			() -> new BaseException("존재하지 않는 수정안입니다. ID : " + id));

		// 수정 요청한 사용자가 수정안의 작성자와 일치하는지 검증
		if (!amendment.getMember().getId().equals(memberId)) {
			throw new BaseException("수정 권한이 없습니다. 사용자 ID: " + memberId);
		}

		//내부에서 status를 검사함
		try {
			Amendment update = amendment.updateContent(updateAmendmentRequest.getTitle(),
				updateAmendmentRequest.getDescription(), updateAmendmentRequest.getHeading(),
				updateAmendmentRequest.getSectionTitle(), updateAmendmentRequest.getSectionContent());

			return AmendmentResponse.of(update);
		} catch (IllegalStateException e) {
			throw new BaseException("수정 요청 후에는 수정안을 수정할 수 없습니다.");
		}
	}
}
