package goorm.eagle7.stelligence.domain.document;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.document.dto.DocumentResponse;
import goorm.eagle7.stelligence.domain.document.dto.SectionRequest;
import goorm.eagle7.stelligence.domain.document.dto.SectionResponse;
import goorm.eagle7.stelligence.domain.document.model.Document;
import goorm.eagle7.stelligence.domain.section.SectionRepository;
import goorm.eagle7.stelligence.domain.section.model.Heading;
import goorm.eagle7.stelligence.domain.section.model.Section;
import goorm.eagle7.stelligence.domain.section.model.SectionId;
import goorm.eagle7.stelligence.domain.section.sequence.SectionIdSequenceRepository;
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
public class DocumentService {

	private final DocumentRepository documentRepository;
	private final SectionRepository sectionRepository;
	private final SectionIdSequenceRepository sectionIdSequenceRepository;

	/**
	 * Document를 생성합니다.
	 * @param title
	 * @param sectionRequests 생성할 섹션 정보
	 */
	@Transactional
	public Long createDocument(String title, List<SectionRequest> sectionRequests) {
		//document 생성
		Document document = Document.createDocument(title);
		documentRepository.save(document);

		//document의 sectionId Sequence 를 생성
		sectionIdSequenceRepository.createSequence(document.getId());

		//section 생성
		for (int order = 0; order < sectionRequests.size(); order++) {
			Section section = Section.createSection(document,
				sectionIdSequenceRepository.getAndIncrementSectionId(document.getId()), 1L,
				sectionRequests.get(order).getHeading(), sectionRequests.get(order).getTitle(),
				sectionRequests.get(order).getContent(), order + 1);

			sectionRepository.save(section);
		}

		return document.getId();
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
	 * 추후 MergeService로 분리될 예정입니다.
	 * @param documentId
	 */
	@Transactional
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
					sectionIdSequenceRepository.getAndIncrementSectionId(document.getId()),
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
	 * @param documentId
	 * @return
	 */
	public DocumentResponse getDocument(Long documentId) {
		Document document = documentRepository.findById(documentId)
			.orElseThrow(() -> new BaseException("문서가 존재하지 않습니다. 문서 ID : " + documentId));

		//getDocument에서 재사용
		//추후 Redis에 캐싱할 예정
		return getDocument(documentId, document.getCurrentRevision());
	}

	/**
	 * Document의 특정 버전을 조회합니다.
	 */
	public DocumentResponse getDocument(Long documentId, Long revision) {

		Document document = documentRepository.findById(documentId)
			.orElseThrow(() -> new BaseException("문서가 존재하지 않습니다. 문서 ID : " + documentId));

		List<SectionResponse> sections = sectionRepository.findByVersion(document, revision)
			.stream()
			.map(SectionResponse::of)
			.toList();

		return DocumentResponse.of(document.getId(), document.getTitle(), sections);
	}
}