package goorm.eagle7.stelligence.domain.document;

import org.springframework.stereotype.Service;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.repository.DebateRepository;
import goorm.eagle7.stelligence.domain.document.content.DocumentContentRepository;
import goorm.eagle7.stelligence.domain.document.content.dto.DocumentStatusResponse;
import lombok.RequiredArgsConstructor;

/**
 * Document의 상태를 조회하는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class DocumentStatusService {

	private final DocumentContentRepository documentContentRepository;
	private final ContributeRepository contributeRepository;
	private final DebateRepository debateRepository;

	/**
	 * Document의 상태를 조회합니다.
	 * @param documentId : 조회할 Document의 ID
	 * @return DocumentStatusResponse
	 */
	public DocumentStatusResponse getDocumentStatus(Long documentId) {
		//문서 존재 여부 확인
		if (!documentContentRepository.existsById(documentId)) {
			throw new BaseException("존재하지 않는 문서입니다. 문서 ID : " + documentId);
		}

		//최신 Contribute, Debate 조회
		Contribute latestContribute = contributeRepository.findLatestContributeByDocumentId(documentId).orElse(null);
		Debate latestDebate = debateRepository.findLatestDebateByDocumentId(documentId).orElse(null);

		return DocumentStatusResponse.of(documentId, latestContribute, latestDebate);
	}
}
