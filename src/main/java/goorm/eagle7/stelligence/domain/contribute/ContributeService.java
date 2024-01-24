package goorm.eagle7.stelligence.domain.contribute;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.amendment.AmendmentService;
import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentRequest;
import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.contribute.dto.ContributeRequest;
import goorm.eagle7.stelligence.domain.contribute.dto.ContributeResponse;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.document.content.DocumentContentRepository;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContributeService {

	private final ContributeRepository contributeRepository;
	private final AmendmentService amendmentService;
	private final MemberRepository memberRepository;
	private final DocumentContentRepository documentContentRepository;

	/**
	 * Contribute 생성
	 * @param contributeRequest
	 * @param loginMemberId
	 * @return
	 */
	@Transactional
	public ContributeResponse createContribute(ContributeRequest contributeRequest, Long loginMemberId) {
		Member member = memberRepository.findById(loginMemberId).orElseThrow(
			() -> new BaseException("존재하지 않는 회원의 요청입니다. 사용자 ID: " + loginMemberId)
		);

		Document document = documentContentRepository.findById(contributeRequest.getDocumentId()).orElseThrow(
			() -> new BaseException("존재하지 않는 문서의 요청입니다. 문서 ID: " + contributeRequest.getDocumentId())
		);

		Contribute contribute = Contribute.createContribute(
			member,
			document,
			contributeRequest.getTitle(),
			contributeRequest.getDescription()
		);

		for (AmendmentRequest request : contributeRequest.getAmendments()) {
			Amendment amendment = amendmentService.processAmendment(request);
			contribute.addAmendment(amendment);
		}

		contributeRepository.save(contribute);  // Contribute 저장. 연관된 Amendment도 함께 저장.
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
	 * Contribute 목록 조회: 문서별로 조회
	 * @param documentId
	 * @return
	 */
	public List<ContributeResponse> getContributesByDocument(Long documentId, Pageable pageable) {

		List<Contribute> contributesByDocument =
			contributeRepository.findContributesByDocument(documentId, pageable);

		return contributesByDocument.stream()
			.map(ContributeResponse::of)
			.toList();
	}

	/**
	 * Contribute 목록 조회: 투표중인 Contribute만 조회
	 * @return
	 */
	public List<ContributeResponse> getVotingContributes(Pageable pageable) {

		List<Contribute> votingContributes = contributeRepository.findVotingContributes(pageable);

		return votingContributes.stream()
			.map(ContributeResponse::of)
			.toList();
	}
}