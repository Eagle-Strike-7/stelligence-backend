package goorm.eagle7.stelligence.domain.document.content;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.common.sequence.SectionIdGenerator;
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

	/**
	 * Document를 생성합니다.
	 * DocumentService 내에서만 호출되어야 합니다.
	 * 외부에서 호출시 DocumentGraph와 Content 간 일관성이 깨지는 문제가 발생될 수 있습니다.
	 * @param title 문서의 제목
	 * @param rawContent 사용자가 작성한 글 내용
	 */
	@Transactional
	public Document createDocument(String title, String rawContent, Member author) {
		log.trace("DocumentService.createDocument called");

		//document 생성
		Document document = Document.createDocument(title, author);
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

		return getDocument(documentId, document.getCurrentRevision());
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
		if (revision > document.getCurrentRevision()) {
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

		return DocumentResponse.of(document, sections, contributors);
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

}
