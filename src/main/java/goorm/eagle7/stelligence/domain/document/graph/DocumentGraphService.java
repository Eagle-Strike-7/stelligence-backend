package goorm.eagle7.stelligence.domain.document.graph;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
	 * (기존에 설정된 그래프 관련 캐시를 모두 무효화합니다.)
	 * @param document: 생성된 문서를 나타냅니다.
	 */
	@Transactional
	@CacheEvict(value="RootGraph", allEntries = true, cacheManager = "cacheManager")
	public void createDocumentNode(Document document) {

		DocumentNode documentNode = new DocumentNode(document.getId(), document.getTitle());
		documentNodeRepository.save(documentNode);
	}

	/**
	 * 생성된 Document 객체를 기준으로 새로운 문서 노드를 생성합니다.
	 * 추가로, parentDocumentId를 갖는 문서 노드와 생성된 문서 노드 간의 링크를 연결합니다.
	 * (기존에 설정된 그래프 관련 캐시를 모두 무효화합니다.)
	 * @param document: 생성된 문서를 나타냅니다.
	 * @param parentDocumentId: 링크를 연결할 상위 문서의 id를 나타냅니다.
	 */
	@Transactional
	@CacheEvict(value="RootGraph", allEntries = true, cacheManager = "cacheManager")
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
	 * (비슷한 요청이 반복될 것으로 예상되어 데이터를 캐싱합니다.)
	 * @return DocumentGraphResponse: 문서 그래프와 관련된 응답 DTO입니다.
	 */
	@Cacheable(value = "RootGraph", cacheManager = "cacheManager")
	public DocumentGraphResponse findAllGraph() {

		List<DocumentNodeResponse> documentNodes = documentNodeRepository.findAllDocumentNode();
		List<HasChildRelationshipResponse> hasChildRelationshipList = documentNodeRepository.findAllHasChildRelationship();

		return DocumentGraphResponse.of(documentNodes, hasChildRelationshipList);
	}

	/**
	 * 특정 제목으로 문서를 검색합니다.
	 * @param title: 검색할 제목을 나타냅니다.
	 * @param limit: 최대로 검색할 노드의 개수를 나타냅니다.
	 * @return List&lt;DocumentNodeResponse&gt;: 문서 노드에 대한 응답입니다.
	 */
	public List<DocumentNodeResponse> findNodeByTitle(String title, int limit) {

		return documentNodeRepository.findNodeByTitle(title, limit);
	}

	/**
	 * documentId의 리스트와 일치하는 문서들을 검색합니다.
	 * @param documentIdList: 검색할 문서들의 id들을 담은 리스트입니다.
	 * @return List&lt;DocumentNodeResponse&gt;: 문서 노드에 대한 응답입니다.
	 */
	public List<DocumentNodeResponse> findNodeByDocumentId(List<Long> documentIdList) {

		return documentNodeRepository.findNodeByDocumentId(documentIdList);
	}

	/**
	 * 처음 그래프를 조회할 때에는 루트 노드로부터 특정 깊이까지를 조회할 수 있어야합니다.
	 * (비슷한 요청이 반복될 것으로 예상되어 데이터를 캐싱합니다.)
	 * @param depth: 루트 노드로부터 몇 번째 깊이까지를 조회할 것인지를 결정합니다.
	 * @return DocumentGraphResponse: 문서 그래프와 관련된 응답 DTO입니다.
	 */
	@Cacheable(value = "RootGraph", key = "#depth", cacheManager = "cacheManager")
	public DocumentGraphResponse findFromRootNodesWithDepth(int depth) {

		List<DocumentNodeResponse> documentNodes = documentNodeRepository.findDocumentNodeFromRootWithDepth(depth);
		List<HasChildRelationshipResponse> hasChildRelationshipList = documentNodeRepository.findHasChildRelationshipFromRootWithDepth(depth);

		return DocumentGraphResponse.of(documentNodes, hasChildRelationshipList);
	}

	/**
	 * 문서 ID에 따라 특정 문서 노드를 삭제합니다.
	 * 이때, 해당 문서의 하위 문서의 링크와 그룹을 함께 재정의합니다.
	 * (기존에 설정된 그래프 관련 캐시를 모두 무효화합니다.)
	 * @param documentId: 삭제할 문서의 ID입니다.
	 */
	@Transactional
	@CacheEvict(value="RootGraph", allEntries = true, cacheManager = "cacheManager")
	public void deleteDocumentNode(Long documentId) {

		boolean isRoot = documentNodeRepository.isRootNode(documentId)
			.orElseThrow(() -> new IllegalArgumentException("삭제하려는 노드가 존재하지 않습니다."));

		if (isRoot) {
			documentNodeRepository.deleteRootNodeByDocumentId(documentId);
		} else {
			documentNodeRepository.deleteNonrootNodeByDocumentId(documentId);
		}
	}

	/**
	 * documentId에 따라 특정 문서를 찾습니다.
	 * 찾은 특정 문서의 상위 문서를 parentDocumentId를 갖는 문서로 변경합니다.
	 * parentDocumentId가 null이라면 링크를 삭제합니다.
	 * (기존에 설정된 그래프 관련 캐시를 모두 무효화합니다.)
	 * @param documentId: 링크를 변경할 문서 ID
	 * @param parentDocumentId: 링크를 연결할 문서 ID
	 */
	@Transactional
	@CacheEvict(value="RootGraph", allEntries = true, cacheManager = "cacheManager")
	public void updateDocumentLink(Long documentId, Long parentDocumentId) {
		if (parentDocumentId != null) {
			changeLinkToParent(documentId, parentDocumentId);
		} else {
			removeLink(documentId);
		}
	}

	@Transactional
	@CacheEvict(value="RootGraph", allEntries = true, cacheManager = "cacheManager")
	public void changeTitle(Long documentId, String updateTitle) {

		DocumentNode documentNode = documentNodeRepository.findById(documentId)
			.orElseThrow(() -> new BaseException("존재하지 않는 노드에 대한 제목 수정 요청입니다. 문서 ID: " + documentId));

		if (documentNode.getTitle().equals(updateTitle)) {
			log.debug("문서의 제목이 변경되지 않았습니다. 문서ID: {}, 문서제목: {}", documentId, updateTitle);
			return;
		}

		if (documentNode.getParentDocumentNode() == null) {
			documentNodeRepository.updateRootNodeTitle(documentId, updateTitle);
		}
		documentNodeRepository.updateNonrootNodeTitle(documentId, updateTitle);

	}

	/**
	 * documentId에 따라 특정 문서를 찾아서 링크를 제거합니다.
	 * 그리고, parentDocumentId에 따라 다른 문서와의 링크를 추가합니다.
	 * @param documentId: 링크를 수정할 문서의 ID
	 * @param parentDocumentId: 링크를 추가할 문서의 ID
	 */
	private void changeLinkToParent(Long documentId, Long parentDocumentId) {
		Optional<DocumentNode> documentOptional = documentNodeRepository.findSingleNodeByDocumentId(documentId);
		Optional<DocumentNode> parentDocumentOptional = documentNodeRepository.findSingleNodeByDocumentId(
			parentDocumentId);
		if (documentOptional.isEmpty() || parentDocumentOptional.isEmpty()) {
			throw new IllegalArgumentException("존재하지 않는 노드에 대한 요청입니다.");
		}

		documentNodeRepository.changeLinkToUpdateParent(documentId, parentDocumentId);
	}

	/**
	 * documentId에 따라 특정 문서를 찾아서 링크를 제거합니다.
	 * @param documentId: 링크를 제거할 문서의 ID
	 */
	private void removeLink(Long documentId) {
		Optional<DocumentNode> documentOptional = documentNodeRepository.findSingleNodeByDocumentId(documentId);
		if (documentOptional.isEmpty()) {
			throw new IllegalArgumentException("존재하지 않는 노드에 대한 요청입니다.");
		}
		documentNodeRepository.removeLink(documentId);
	}
}
