package goorm.eagle7.stelligence.domain.document;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.domain.document.content.DocumentContentService;
import goorm.eagle7.stelligence.domain.document.content.dto.DocumentResponse;
import goorm.eagle7.stelligence.domain.document.content.dto.SectionResponse;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.document.dto.DocumentCreateRequest;
import goorm.eagle7.stelligence.domain.document.graph.DocumentGraphService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 서로다른 Document 관련 서비스를 하나로 묶어주는 서비스입니다.
 * 관련 링크 : https://lktprogrammer.tistory.com/42
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DocumentService {

	private final DocumentContentService documentContentService;
	private final DocumentGraphService documentGraphService;

	/**
	 * Document를 생성합니다.
	 * RDB에 Document를 생성한 뒤, 해당 ID를 기준으로 DocumentNode를 생성합니다.
	 *
	 * 두 요청을 하나의 트랜잭션으로 묶어 일관성을 보장합니다.
	 * @param documentCreateRequest
	 * @return
	 */
	public DocumentResponse createDocument(DocumentCreateRequest documentCreateRequest) {

		//DocumentContent 저장
		Document createdDocument = documentContentService.createDocument(documentCreateRequest.getTitle(),
			documentCreateRequest.getContent());

		//DocumentLink 저장
		documentGraphService.createDocumentNode(createdDocument);

		//DocumentResponse의 SectionResponse는 Document 엔티티와 연결된 Sections를 따르지 않습니다. (버전별로 상이한 경우가 있기 때문)
		List<SectionResponse> sections = createdDocument.getSections().stream().map(SectionResponse::of).toList();
		return DocumentResponse.of(createdDocument, sections);
	}

	/**
	 * Document를 조회합니다.
	 * @param documentId : 조회할 Document의 ID
	 * @param revision : null이면 최신 버전을 조회합니다.
	 * @return
	 */
	public DocumentResponse getDocumentContent(Long documentId, Long revision) {
		if (revision == null) {
			return documentContentService.getDocument(documentId);
		} else {
			return documentContentService.getDocument(documentId, revision);
		}
	}

}
