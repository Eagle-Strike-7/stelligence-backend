package goorm.eagle7.stelligence.domain.contribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentRequest;
import goorm.eagle7.stelligence.domain.amendment.model.AmendmentType;
import goorm.eagle7.stelligence.domain.contribute.dto.ContributeRequest;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.repository.DebateRepository;
import goorm.eagle7.stelligence.domain.document.content.DocumentContentRepository;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.section.SectionRepository;
import lombok.RequiredArgsConstructor;

/**
 * ContributeRequest의 유효성을 검증하는 클래스입니다.
 */
@Component
@RequiredArgsConstructor
public class ContributeRequestValidator {

	private final ContributeRepository contributeRepository;
	private final DocumentContentRepository documentContentRepository;
	private final SectionRepository sectionRepository;
	private final DebateRepository debateRepository;

	/**
	 * 수정요청의 유효성을 검증합니다.
	 * <p>검증 실패 케이스
	 * <ul>
	 *     <li>document가 존재하지 않을 경우</li>
	 *     <li>해당 document에 대한 투표중인 수정요청이 존재하는 경우</li>
	 *     <li>해당 document에 대한 토론이 진행중인 경우</li>
	 *     <li>해당 document에 대한 토론이 종료되고 참여자를 대상으로 수정요청 대기중에 허용되지 않은 토론이나 참여자가 요청하는 경우</li>
	 *     <li>수정하고자 하는 section들이 document에 존재하지 않을 경우</li>
	 *     <li>수정하고자 하는 제목이 다른 문서와 중복되는 경우</li>
	 *     <li>수정하고자 하는 제목에 대해 다른 수정요청이 해당 제목으로 변경을 요청중인 경우</li>
	 *     <li>각각의 sectionId를 가진 amendmentRequest들이 creatingOrder가 중복되거나 순차적이지 않을 경우</li>
	 * </ul>
	 * </p>
	 *
	 * @param request       수정요청 DTO
	 * @param loginMemberId
	 * @throws BaseException 유효하지 않은 요청일 경우
	 */
	@Transactional(readOnly = true)
	public void validate(ContributeRequest request, Long loginMemberId) {

		//document가 존재하는가
		Document document = documentContentRepository.findById(request.getDocumentId())
			.orElseThrow(() -> new BaseException("문서가 존재하지 않습니다. documentId=" + request.getDocumentId()));

		//해당 document에 대한 수정요청이 이미 존재하는가 (투표중인가)
		if (contributeRepository.existsByDocumentAndStatus(document, ContributeStatus.VOTING)) {
			throw new BaseException("이미 해당 문서에 대한 수정요청이 존재합니다. documentId=" + request.getDocumentId());
		}

		// 해당 document에 대한 토론이 진행중이거나, 수정요청 대기중인가?
		checkDebate(request, loginMemberId);

		//수정하고자 하는 section들이 document에 존재하는가
		List<Long> sectionIds = new ArrayList<>(
			sectionRepository.findSectionIdByVersion(document, document.getLatestRevision()));

		sectionIds.add(0L); // Phantom Section Id
		
		request.getAmendments().stream().map(AmendmentRequest::getSectionId).forEach(
			sectionId -> {
				if (!sectionIds.contains(sectionId)) {
					throw new BaseException("해당 문서에 존재하지 않는 섹션을 수정하려고 합니다. sectionId=" + sectionId);
				}
			}
		);

		//Phantom Section에 대한 수정요청은 항상 CREATE 타입이어야 한다.
		if (request.getAmendments().stream().anyMatch(
			req -> req.getSectionId().equals(0L)
				&& !req.getType().equals(AmendmentType.CREATE))) {
			throw new BaseException("Section 0에 대한 수정요청은 항상 CREATE 타입이어야 합니다.");
		}

		//이미 제목을 가진 문서가 존재하는가 - contribute의 대상이 되는 document라면 허용
		Optional<Document> optionalDocument = documentContentRepository.findByTitle(request.getAfterDocumentTitle());
		if (optionalDocument.isPresent() && !optionalDocument.get().getId().equals(request.getDocumentId())) {
			throw new BaseException("이미 해당 제목을 가진 문서가 존재합니다. title=" + request.getAfterDocumentTitle());
		}

		if (contributeRepository.existsDuplicateRequestedDocumentTitle(request.getAfterDocumentTitle())) {
			throw new BaseException("해당 제목으로 변경을 요청중인 수정요청이 이미 존재합니다. title=" + request.getAfterDocumentTitle());
		}

		//각각의 sectionId를 가진 amendmentRequest들이 creatingOrder를 중복되지 않으면서 순차적인 값을 가지고 있는가
		Map<Long, List<Integer>> sectionOrders = request.getAmendments().stream()
			.filter(
				amendmentRequest -> amendmentRequest.getType() == AmendmentType.CREATE) //creatingOrder의 검증은 CREATE에 한함
			.collect(Collectors.groupingBy(
				AmendmentRequest::getSectionId,
				Collectors.mapping(AmendmentRequest::getCreatingOrder, Collectors.toList())
			));

		for (Map.Entry<Long, List<Integer>> entry : sectionOrders.entrySet()) {
			List<Integer> orders = entry.getValue();
			Collections.sort(orders); // 정렬

			// 중복 검사
			if (hasDuplicates(orders)) {
				throw new BaseException("중복된 생성 순서가 존재합니다. sectionId=" + entry.getKey());
			}

			// 순차적인지 검사
			if (!isSequential(orders)) {
				throw new BaseException("생성 순서가 순차적이지 않습니다.");
			}
		}
	}

	private boolean hasDuplicates(List<Integer> orders) {
		Set<Integer> uniqueOrders = new HashSet<>(orders);
		return uniqueOrders.size() != orders.size();
	}

	private boolean isSequential(List<Integer> orders) {
		for (int i = 0; i < orders.size(); i++) {
			if (orders.get(i) != i + 1) { // 1부터 시작하므로 i + 1과 비교
				return false;
			}
		}
		return true;
	}

	private void checkDebate(ContributeRequest request, Long loginMemberId) {
		debateRepository.findLatestDebateByDocumentId(request.getDocumentId()).ifPresent(
			debate -> {
				// 토론이 진행중인가?
				checkIsOnDebate(debate);
				// 토론이 종료된 후 토론 참여자를 위한 수정요청 대기중인가?
				checkDebateIsPendingForDebater(debate, request.getRelatedDebateId(), loginMemberId);
			}
		);
	}

	void checkIsOnDebate(Debate debate) {
		if (debate.isOnDebate()) {
			throw new BaseException("해당 문서에 대한 토론이 진행중입니다. debateId=" + debate.getId());
		}
	}

	void checkDebateIsPendingForDebater(Debate debate, Long relatedDebateId, Long loginMemberId) {
		if (debate.isPendingForContribute()
			// 다른 토론에서 파생된 수정요청을 보내는 경우
			&& (!debate.getId().equals(relatedDebateId)
			// 토론 참여자가 아닌 사람이 수정요청을 보내는 경우
			|| !debate.hasPermissionToWriteDrivenContribute(loginMemberId))) {
			throw new BaseException("최근 종료된 토론 참여자를 위한 수정요청 대기중입니다. debateId=" + debate.getId());
		}
	}

}
