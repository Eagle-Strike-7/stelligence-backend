package goorm.eagle7.stelligence.domain.document.content;

import static goorm.eagle7.stelligence.domain.document.content.dto.DocumentResponseOuterClass.*;
import static goorm.eagle7.stelligence.domain.document.content.dto.SectionResponseOuterClass.*;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.common.sequence.SectionIdGenerator;
import goorm.eagle7.stelligence.domain.document.content.dto.SectionRequest;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.document.content.parser.DocumentParser;
import goorm.eagle7.stelligence.domain.section.SectionRepository;
import goorm.eagle7.stelligence.domain.section.model.Heading;
import goorm.eagle7.stelligence.domain.section.model.Section;
import goorm.eagle7.stelligence.domain.section.model.SectionId;
import lombok.AllArgsConstructor;
import lombok.Data;
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
	 * @param title 문서의 제목
	 * @param rawContent 사용자가 작성한 글 내용
	 */
	@Transactional
	public Document createDocument(String title, String rawContent) {
		//document 생성
		Document document = Document.createDocument(title);
		documentRepository.save(document);

		List<SectionRequest> sectionRequests = documentParser.parse(rawContent);

		//section 생성
		for (int order = 0; order < sectionRequests.size(); order++) {
			Section section = Section.createSection(
				document,
				sectionIdGenerator.getAndIncrementSectionId(),
				1L,
				sectionRequests.get(order).getHeading(),
				sectionRequests.get(order).getTitle(),
				sectionRequests.get(order).getContent(),
				order + 1
			);

			sectionRepository.save(section);
		}

		return document;
	}

	/**
	 * Contribute가 개발되기 전이므로 임시로 생성한 객체입니다.
	 */
	@Data
	@AllArgsConstructor
	public static class Commit {
		private String type;
		private Long targetSectionId;
		private Long targetSectionRevision;
		private Heading heading;
		private String title;
		private String content;
	}

	/**
	 * 높은 투표율을 받은 Contribute에 대해서 Merge를 진행합니다.
	 * Contribute를 파라미터로 받아야하지만, 현재 개발되지 않았으므로 임시 파라미터를 받습니다.
	 *
	 * MergeService로 로직이 분리되었습니다.
	 * 하지만 다른 테스트가 현재 mergeContribute에 의존하고 있으므로
	 * 테스트 데이터 구축된 이후 없애도록 하겠습니다.
	 *
	 * @param documentId
	 */
	@Transactional
	@Deprecated
	public void mergeContribute(Long documentId, List<Commit> commits) {
		Document document = documentRepository.findForUpdate(documentId)
			.orElseThrow(() -> new BaseException("문서가 존재하지 않습니다. 문서 ID : " + documentId));

		Long newRevision = document.getCurrentRevision() + 1;

		/**
		 * Contribute에 포함되어있는 commit들을 하나씩 반영
		 */
		for (Commit commit : commits) {

			Section targetSection = sectionRepository.findById(
					SectionId.of(commit.targetSectionId, commit.targetSectionRevision))
				.orElseThrow(() -> new BaseException("존재하지 않는 섹션입니다. 섹션 ID : " + commit.targetSectionId));

			//Commit Type에 따라 분기
			if (commit.type.equals("INSERT")) {
				Section section = Section.createSection(
					document,
					sectionIdGenerator.getAndIncrementSectionId(),
					newRevision,
					commit.getHeading(),
					commit.getTitle(),
					commit.getContent(),
					targetSection.getOrder() + 1
				);

				sectionRepository.save(section);

				sectionRepository.updateOrders(document.getId(), document.getCurrentRevision(), section.getOrder());

			} else if (commit.type.equals("UPDATE")) {
				Section section = Section.createSection(
					document,
					commit.targetSectionId, //기존 섹션의 ID를 그대로 사용합니다.
					newRevision,
					commit.getHeading(),
					commit.getTitle(),
					commit.getContent(),
					targetSection.getOrder()
				);

				sectionRepository.save(section);

			} else if (commit.type.equals("DELETE")) {
				Section section = Section.createSection(
					document,
					targetSection.getId(),
					newRevision,
					null,
					null,
					null,
					targetSection.getOrder()
				);

				sectionRepository.save(section);
			}
		}

		document.incrementCurrentRevision();
	}

	/**
	 * 최신 Document를 조회합니다.
	 *
	 * Redis에 캐싱할 예정입니다.
	 * 캐시는 Document에 대한 수정요청이 반영되는 시점에 invalidate 됩니다.
	 * @param documentId
	 * @return
	 */
	@Cacheable(value = "document", key = "#documentId", cacheManager = "cacheManager")
	public DocumentResponse getDocument(Long documentId) {
		Document document = documentRepository.findById(documentId)
			.orElseThrow(() -> new BaseException("문서가 존재하지 않습니다. 문서 ID : " + documentId));

		return getDocument(documentId, document.getCurrentRevision());
	}

	/**
	 * Document의 특정 버전을 조회합니다.
	 */
	public DocumentResponse getDocument(Long documentId, Long revision) {

		//문서가 존재하는지 확인합니다.
		Document document = documentRepository.findById(documentId)
			.orElseThrow(() -> new BaseException("문서가 존재하지 않습니다. 문서 ID : " + documentId));

		//해당 버전의 섹션들을 조회합니다.
		List<SectionResponse> sections = sectionRepository.findByVersion(document, revision)
			.stream()
			.sorted()
			.map(SectionResponse::of)
			.toList();

		return DocumentResponse.of(document, sections);
	}

	/**
	 * 특정 문자열을 포함하는 Document의 ID를 조회합니다. 최신 버전의 섹션만 조사의 대상이 됩니다.
	 * @param keyword 검색할 키워드
	 * @return 검색된 Document의 ID 목록
	 */
	public List<Long> findDocumentWhichContainsKeyword(String keyword) {
		return documentRepository.findDocumentIdWhichContainsKeywordInLatestVersion(keyword);
	}

}
