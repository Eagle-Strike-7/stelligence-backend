package goorm.eagle7.stelligence.domain.document;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.document.content.DocumentContentService;
import goorm.eagle7.stelligence.domain.document.content.dto.DocumentResponse;
import goorm.eagle7.stelligence.domain.document.content.dto.SectionResponse;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.document.dto.DocumentCreateRequest;
import goorm.eagle7.stelligence.domain.document.graph.DocumentGraphService;
import goorm.eagle7.stelligence.domain.document.graph.dto.DocumentGraphResponse;
import goorm.eagle7.stelligence.domain.document.graph.dto.DocumentNodeResponse;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
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
	private final MemberRepository memberRepository;

	/**
	 * Document를 생성합니다.
	 * RDB에 Document를 생성한 뒤, 해당 ID를 기준으로 DocumentNode를 생성합니다.
	 *
	 * 두 요청을 하나의 트랜잭션으로 묶어 일관성을 보장합니다.
	 * @param documentCreateRequest : 생성할 Document의 정보
	 * @return 생성된 DocumentResponse
	 */
	public DocumentResponse createDocument(DocumentCreateRequest documentCreateRequest, Long loginMemberId) {

		Member author = memberRepository.findById(loginMemberId)
			.orElseThrow(() -> new BaseException("존재하지 않는 사용자입니다. 사용자 ID : " + loginMemberId));

		//DocumentContent 저장
		Document createdDocument = documentContentService.createDocument(documentCreateRequest.getTitle(),
			documentCreateRequest.getContent(), author);

		//DocumentLink 저장 - 지정한 부모 문서가 있다면 링크 연결
		if (documentCreateRequest.getParentDocumentId() == null) {
			documentGraphService.createDocumentNode(createdDocument);
		} else {
			documentGraphService.createDocumentNodeWithParent(createdDocument,
				documentCreateRequest.getParentDocumentId());
		}

		//사용자의 기여 횟수를 증가시킵니다.
		author.incrementContributes();

		//DocumentResponse를 생성합니다.
		/*
		 * Document가 Section의 목록을 가지고 있으며 이 값을 Response로 만드는 행동은 최초 생성시에만 유효합니다.
		 * 추후에 코드가 변경될 여지가 있습니다. 자세한 내용은 Document.sections의 주석을 참고해주세요.
		 */
		List<SectionResponse> sections = createdDocument.getSections().stream().map(SectionResponse::of).toList();
		return DocumentResponse.of(createdDocument, sections, Collections.emptyList(), true);
	}

	/**
	 * Document를 조회합니다.
	 * @param documentId : 조회할 Document의 ID
	 * @param revision : null이면 최신 버전을 조회합니다.
	 * @return DocumentResponse
	 */
	public DocumentResponse getDocumentContent(Long documentId, Long revision) {
		if (revision == null) {
			return documentContentService.getDocument(documentId);
		} else {
			return documentContentService.getDocument(documentId, revision);
		}
	}

	/**
	 * 문서 그래프를 조회합니다.
	 * documentId가 null 이라면 최상위 문서를 기준으로 조회합니다.
	 * 조회 대상 문서로부터 일정 깊이의 하위 문서까지 함께 조회합니다.
	 * @param documentId: 조회 대상이 되는 문서의 id입니다.
	 * @param depth: 함께 조회할 문서의 깊이입니다.
	 * @return DocumentGraphResponse
	 */
	public DocumentGraphResponse getDocumentGraph(Long documentId, int depth) {
		if (documentId == null) {
			return documentGraphService.findFromRootNodesWithDepth(depth);
		} else {
			return documentGraphService.findGraphWithDepth(documentId, depth);
		}
	}

	/**
	 * 전체 문서 그래프를 조회합니다.
	 * @return DocumentGraphResponse
	 */
	public DocumentGraphResponse getAllDocumentGraph() {
		return documentGraphService.findAllGraph();
	}

	/**
	 * 문서의 제목을 기준으로 그래프 노드를 조회합니다.
	 * @param title: 문서의 제목
	 * @param limit: 조회할 최대 결과의 개수를 제한합니다.
	 * @return List&lt;DocumentNodeResponse&gt;
	 */
	public List<DocumentNodeResponse> getDocumentNodeByTitle(String title, int limit) {
		return documentGraphService.findNodeByTitle(title, limit);
	}
}
