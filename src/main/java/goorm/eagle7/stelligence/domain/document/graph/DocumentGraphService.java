package goorm.eagle7.stelligence.domain.document.graph;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.document.graph.dto.DocumentGraphResponse;
import goorm.eagle7.stelligence.domain.document.graph.dto.DocumentNodeResponse;
import goorm.eagle7.stelligence.domain.document.graph.dto.HasChildRelationshipResponse;
import goorm.eagle7.stelligence.domain.document.graph.model.DocumentNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DocumentGraphService {

	private final DocumentNodeRepository documentNodeRepository;

	/**
	 * 생성된 Document 객체를 기준으로 새로운 문서 노드를 생성합니다.
	 * 상위 문서가 없는 최상위 문서에 대한 노드를 생성할 때민 사용합니다.
	 * 상위 문서가 존재하는 문서 노드는 {@link #createDocumentNodeWithParent} 메서드를 통해 생성해주세요.
	 * @param document: 생성된 문서를 나타냅니다.
	 */
	@Transactional
	public void createDocumentNode(Document document) {

		DocumentNode documentNode = new DocumentNode(document.getId(), document.getTitle());
		documentNodeRepository.save(documentNode);
	}

	/**
	 * 생성된 Document 객체를 기준으로 새로운 문서 노드를 생성합니다.
	 * 추가로, parentDocumentId를 갖는 문서 노드와 생성된 문서 노드 간의 링크를 연결합니다.
	 * @param document: 생성된 문서를 나타냅니다.
	 * @param parentDocumentId: 링크를 연결할 상위 문서의 id를 나타냅니다.
	 */
	@Transactional
	public void createDocumentNodeWithParent(Document document, Long parentDocumentId) {

		DocumentNode parentDocumentNode = documentNodeRepository.findSingleNodeByDocumentId(parentDocumentId)
			.orElseThrow(() -> new BaseException("링크를 연결할 문서가 존재하지 않습니다."));
		DocumentNode documentNode = new DocumentNode(document.getId(), document.getTitle(), parentDocumentNode);
		documentNodeRepository.save(documentNode);
	}

	/**
	 * 툭정 문서와 그 문서로부터 n번째 깊이의 문서까지 함께 조회합니다.
	 * @param documentId: 찾으려는 특정 문서의 id를 나타냅니다.
	 * @param depth: 어느 깊이의 문서까지 가져올지를 결정합니다. depth가 0이면 자기자신만 반환합니다.
	 * @return DocumentGraphResponse: 문서 그래프와 관련된 응답 DTO입니다.
	 */
	public DocumentGraphResponse findGraphWithDepth(Long documentId, int depth) {

		List<DocumentNodeResponse> documentNodes = documentNodeRepository.findDocumentNodeByDocumentIdWithDepth(documentId, depth);
		List<HasChildRelationshipResponse> hasChildRelationshipList = documentNodeRepository.findHasChildRelationshipByDocumentIdWithDepth(documentId, depth);

		return DocumentGraphResponse.of(documentNodes, hasChildRelationshipList);
	}

	/**
	 * 모든 문서와 문서 간의 관계를 조회합니다.
	 * 문서가 많아지면 부하가 발생할 수 있습니다.
	 * @return DocumentGraphResponse: 문서 그래프와 관련된 응답 DTO입니다.
	 */
	public DocumentGraphResponse findAllGraph() {

		List<DocumentNodeResponse> documentNodes = documentNodeRepository.findAllDocumentNode();
		List<HasChildRelationshipResponse> hasChildRelationshipList = documentNodeRepository.findAllHasChildRelationship();

		return DocumentGraphResponse.of(documentNodes, hasChildRelationshipList);
	}
}
