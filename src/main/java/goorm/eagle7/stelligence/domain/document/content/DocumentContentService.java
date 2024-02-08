package goorm.eagle7.stelligence.domain.document.content;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.common.sequence.SectionIdGenerator;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.model.DebateStatus;
import goorm.eagle7.stelligence.domain.debate.repository.DebateRepository;
import goorm.eagle7.stelligence.domain.document.content.dto.DocumentResponse;
import goorm.eagle7.stelligence.domain.document.content.dto.SectionRequest;
import goorm.eagle7.stelligence.domain.document.content.dto.SectionResponse;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.document.content.parser.DocumentParser;
import goorm.eagle7.stelligence.domain.member.dto.MemberSimpleResponse;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.section.SectionRepository;
import goorm.eagle7.stelligence.domain.section.model.Section;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Document와 관련된 비즈니스 로직을 담당하는 서비스입니다.
 * Document의 생성, 조회, Merge를 담당합니다.
 * Merge 부분은 추후 MergeService로 분리될 예정입니다.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DocumentContentService {

	private final DocumentContentRepository documentRepository;
	private final SectionRepository sectionRepository;
	private final SectionIdGenerator sectionIdGenerator;
	private final DocumentParser documentParser;
	private final ContributeRepository contributeRepository;
	private final DebateRepository debateRepository;

	/**
	 * Document를 생성합니다.
	 * DocumentService 내에서만 호출되어야 합니다.
	 * 외부에서 호출시 DocumentGraph와 Content 간 일관성이 깨지는 문제가 발생될 수 있습니다.
	 * @param title 문서의 제목
	 * @param rawContent 사용자가 작성한 글 내용
	 * @param parentDocumentId 부모 문서의 ID : 루트 문서를 생성하는 경우 null
	 */
	@Transactional
	public Document createDocument(String title, String rawContent, Long parentDocumentId, Member author) {
		log.trace("DocumentService.createDocument called");

		//부모 문서가 존재하는지 확인합니다.
		Document parentDocument = parentDocumentId == null ? null : documentRepository.findById(parentDocumentId)
			.orElseThrow(() -> new BaseException("부모 문서가 존재하지 않습니다. 문서 ID : " + parentDocumentId));

		//document 생성
		Document document = Document.createDocument(title, author, parentDocument);
		documentRepository.save(document);

		List<SectionRequest> sectionRequests = documentParser.parse(rawContent);

		//section 생성
		for (int order = 0; order < sectionRequests.size(); order++) {
			Section section = Section.createSection(document, sectionIdGenerator.getAndIncrementSectionId(), 1L,
				sectionRequests.get(order).getHeading(), sectionRequests.get(order).getTitle(),
				sectionRequests.get(order).getContent(), order + 1);

			sectionRepository.save(section);
		}

		return document;
	}

	/**
	 * 최신 Document를 조회합니다.
	 * @param documentId 조회할 Document의 ID
	 * @return 최신 Document의 Response Object
	 */
	@Cacheable(value = "document", key = "#documentId", cacheManager = "cacheManager")
	public DocumentResponse getDocument(Long documentId) {
		Document document = documentRepository.findById(documentId)
			.orElseThrow(() -> new BaseException("문서가 존재하지 않습니다. 문서 ID : " + documentId));

		return getDocument(documentId, document.getLatestRevision());
	}

	/**
	 * 특정 버전의 Document를 조회합니다.
	 * @param documentId 조회할 Document의 ID
	 * @param revision 조회할 Document의 버전
	 * @return 특정 버전의 Document의 Response Object
	 */
	public DocumentResponse getDocument(Long documentId, Long revision) {
		log.trace("DocumentService.getDocument called");

		//문서가 존재하는지 확인합니다.
		Document document = documentRepository.findById(documentId)
			.orElseThrow(() -> new BaseException("문서가 존재하지 않습니다. 문서 ID : " + documentId));

		//버전이 존재하는지 확인합니다.
		if (revision > document.getLatestRevision()) {
			throw new BaseException("존재하지 않는 버전입니다. 버전 : " + revision);
		}

		//해당 버전의 섹션들을 조회합니다.
		List<SectionResponse> sections = sectionRepository.findByVersion(document, revision)
			.stream()
			.sorted()
			.map(SectionResponse::of)
			.toList();

		//해당 문서의 기여자들을 조회합니다.
		List<MemberSimpleResponse> contributors = documentRepository.findContributorsByDocumentId(documentId)
			.stream()
			.map(MemberSimpleResponse::from)
			.toList();

		// 수정 가능 여부를 판별
		// 정확히는 토론 종료 후 1일 동안은 기본적으로 불가능하며, 토론자에게만 수정요청을 받을 수 있도록 만들어야 합니다.
		boolean isVoting = contributeRepository.existsByDocumentAndStatus(document, ContributeStatus.VOTING);
		boolean isDebating = debateRepository.existsByContributeDocumentIdAndStatus(documentId, DebateStatus.OPEN);
		boolean isEditable = !isVoting && !isDebating;

		Contribute latestContribute = contributeRepository.findLatestContributeByDocument(document).orElse(null);
		Debate latestDebate = debateRepository.findLatestDebateByDocumentId(document.getId()).orElse(null);

		return DocumentResponse.of(document, revision, sections, contributors, isEditable);
	}

	/**
	 * 특정 문자열을 포함하는 Document의 ID를 조회합니다. 최신 버전의 섹션만 조사의 대상이 됩니다.
	 * @param keyword 검색할 키워드
	 * @return 키워드를 포함하고 있는 Document의 ID 목록
	 */
	public List<Long> findDocumentWhichContainsKeyword(String keyword) {
		log.trace("DocumentService.findDocumentWhichContainsKeyword called");
		return documentRepository.findDocumentIdWhichContainsKeywordInLatestVersion(keyword);
	}

	/**
	 * 문서의 제목을 변경합니다.
	 * @param documentId 제목을 변경할 문서 ID
	 * @param newTitle 변경할 문서 제목
	 */
	@Transactional
	public void changeTitle(Long documentId, String newTitle) {
		log.trace("DocumentService.changeTitle called");
		Document document = documentRepository.findById(documentId)
			.orElseThrow(() -> new BaseException("문서가 존재하지 않습니다. 문서 ID : " + documentId));
		document.changeTitle(newTitle);
	}

	/**
	 * 상위 문서를 변경합니다.
	 * newParentDocumentId가 null인 경우 상위 문서 참조를 삭제합니다.
	 * @param documentId 상위 문서를 변경하려는 문서 ID
	 * @param newParentDocumentId 상위 문서 ID
	 */
	@Transactional
	public void updateParentDocument(Long documentId, Long newParentDocumentId) {

		Document document = documentRepository.findById(documentId)
			.orElseThrow(() -> new BaseException("문서가 존재하지 않습니다. 문서 ID : " + documentId));

		Document parentDocument = newParentDocumentId == null ? null : documentRepository.findById(newParentDocumentId)
			.orElseThrow(() -> new BaseException("상위 문서가 존재하지 않습니다. 문서 ID : " + newParentDocumentId));

		document.updateParentDocument(parentDocument);
	}
}
