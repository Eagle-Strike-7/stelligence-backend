package goorm.eagle7.stelligence.domain.contribute;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.amendment.AmendmentService;
import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentRequest;
import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.contribute.dto.ContributePageResponse;
import goorm.eagle7.stelligence.domain.contribute.dto.ContributeRequest;
import goorm.eagle7.stelligence.domain.contribute.dto.ContributeResponse;
import goorm.eagle7.stelligence.domain.contribute.dto.ContributeSimpleResponse;
import goorm.eagle7.stelligence.domain.contribute.event.NewContributeEvent;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.repository.DebateRepository;
import goorm.eagle7.stelligence.domain.document.content.DocumentContentRepository;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.vote.VoteRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContributeService {

	private final ContributeRepository contributeRepository;
	private final AmendmentService amendmentService;
	private final MemberRepository memberRepository;
	private final DocumentContentRepository documentContentRepository;
	private final ContributeRequestValidator contributeRequestValidator;
	private final VoteRepository voteRepository;
	private final DebateRepository debateRepository;
	private final ApplicationEventPublisher applicationEventPublisher;

	/**
	 * Contribute 생성
	 * @param contributeRequest
	 * @param loginMemberId
	 * @return
	 */
	@Transactional
	public ContributeResponse createContribute(ContributeRequest contributeRequest, Long loginMemberId) {

		contributeRequestValidator.validate(contributeRequest, loginMemberId);

		Member member = memberRepository.findById(loginMemberId).orElseThrow(
			() -> new BaseException("존재하지 않는 회원의 요청입니다. 사용자 ID: " + loginMemberId)
		);

		Document document = documentContentRepository.findById(contributeRequest.getDocumentId()).orElseThrow(
			() -> new BaseException("존재하지 않는 문서의 요청입니다. 문서 ID: " + contributeRequest.getDocumentId())
		);

		// 부모 문서 ID가 null 이면 afterParentDocument는 null
		Document afterParentDocument = contributeRequest.getAfterParentDocumentId() == null ?
			null : documentContentRepository.findById(contributeRequest.getAfterParentDocumentId())
			.orElseThrow(() -> new BaseException(
				"존재하지 않는 문서의 요청입니다. 부모 문서 ID: " + contributeRequest.getAfterParentDocumentId()));

		// 연관된 토론 ID null 이면 relatedDebate는 null
		Debate relatedDebate = contributeRequest.getRelatedDebateId() == null ?
			null : debateRepository.findById(contributeRequest.getRelatedDebateId())
			.orElseThrow(() -> new BaseException(
				"연관된 토론이 존재하지 않습니다. 토론 ID: " + contributeRequest.getRelatedDebateId()));

		Contribute contribute = Contribute.createContribute(
			member,
			document,
			contributeRequest.getContributeTitle(),
			contributeRequest.getContributeDescription(),
			contributeRequest.getAfterDocumentTitle(),
			afterParentDocument,
			relatedDebate
		);

		for (AmendmentRequest request : contributeRequest.getAmendments()) {
			Amendment amendment = amendmentService.processAmendment(request);
			contribute.addAmendment(amendment);
		}

		contributeRepository.save(contribute);  // Contribute 저장. 연관된 Amendment도 함께 저장.

		// Contribute 생성 이벤트 발행
		applicationEventPublisher.publishEvent(new NewContributeEvent(contribute.getId()));

		return ContributeResponse.of(contribute);
	}

	/**
	 * Contribute 삭제
	 * @param contributeId
	 * @param loginMemberId
	 * @return
	 */
	@Transactional
	public void deleteContribute(Long contributeId, Long loginMemberId) {
		Contribute contribute = contributeRepository.findById(contributeId).orElseThrow(
			() -> new BaseException("존재하지 않는 수정 요청입니다. 수정요청 ID: " + contributeId)
		);

		// Contribute가 투표 중인지 확인
		if (!contribute.isVoting()) {
			throw new BaseException("투표가 완료된 수정 요청은 삭제할 수 없습니다.");
		}

		// Contribute를 생성한 사용자인지 확인
		if (!contribute.hasPermissionToDelete(loginMemberId)) {
			throw new BaseException("수정 요청을 삭제할 권한이 없습니다.");
		}

		contributeRepository.delete(contribute);
	}

	/**
	 * Contribute 조회
	 * @param contributeId
	 * @return
	 */
	public ContributeResponse getContribute(Long contributeId) {
		Contribute contribute = contributeRepository.findByIdWithAmendmentsAndMember(contributeId).orElseThrow(
			() -> new BaseException("존재하지 않는 수정 요청입니다. 수정요청 ID: " + contributeId)
		);

		return ContributeResponse.of(contribute);
	}

	/**
	 * Contribute 목록 조회: 투표 상태별로 조회
	 * @param status
	 * @param pageable
	 * @return
	 */
	public ContributePageResponse getContributesByStatus(ContributeStatus status, Pageable pageable) {

		Page<Contribute> votingContributes = contributeRepository.findByContributeStatus(status, pageable);

		Page<ContributeSimpleResponse> listResponses = votingContributes.map(
			(contribute) -> ContributeSimpleResponse.of(contribute, voteRepository.getVoteSummary(contribute.getId())));

		return ContributePageResponse.from(listResponses);
	}

	/**
	 * Contribute 목록 조회: 투표가 완료된 Contribute만 조회(MERGED, REJECTED, DEBATING)
	 * @param pageable
	 * @return
	 */
	public ContributePageResponse getCompletedContributes(Pageable pageable) {

		Page<Contribute> completedContributes = contributeRepository.findCompleteContributes(pageable);

		Page<ContributeSimpleResponse> listResponses = completedContributes.map(
			(contribute) -> ContributeSimpleResponse.of(contribute, voteRepository.getVoteSummary(contribute.getId())));

		return ContributePageResponse.from(listResponses);
	}

	/**
	 * Contribute 목록 조회: 문서 ID와 merged에 따라 조회
	 * @param documentId
	 * @param merged
	 * @param pageable
	 * @return
	 */
	public ContributePageResponse getContributesByDocumentAndStatus(Long documentId, boolean merged,
		Pageable pageable) {

		Page<Contribute> contributesByDocumentAndStatus = contributeRepository.findByDocumentAndStatus(documentId,
			merged, pageable);

		Page<ContributeSimpleResponse> listResponses = contributesByDocumentAndStatus.map(
			(contribute) -> ContributeSimpleResponse.of(contribute, voteRepository.getVoteSummary(contribute.getId())));

		return ContributePageResponse.from(listResponses);
	}
}