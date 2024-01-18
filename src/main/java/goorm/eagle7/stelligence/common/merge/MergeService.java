package goorm.eagle7.stelligence.common.merge;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.common.merge.handler.AmendmentMergeTemplateMapper;
import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.document.content.DocumentContentRepository;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * MergeService
 * 투표가 종료된 Amendment들을 원본에 반영하기 위한 서비스 클래스입니다.
 * 배치 작업으로부터 호출되어야 합니다.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MergeService {

	private final DocumentContentRepository documentContentRepository;
	private final AmendmentMergeTemplateMapper amendmentMergeTemplateMapper;

	/**
	 * Contribute의 Amendment들을 원본에 반영합니다.
	 *
	 * 해당 캐시 엔트리를 삭제합니다.
	 *
	 * @param documentId 반영될 Document ID
	 * @param contribute 반영할 Contribute
	 */
	@CacheEvict(value = "document", key = "#documentId", cacheManager = "cacheManager")
	public void merge(Long documentId, Contribute contribute) {

		Document document = documentContentRepository.findForUpdate(documentId)
			.orElseThrow(() -> new RuntimeException("Document가 존재하지 않습니다."));

		List<Amendment> amendments = contribute.getAmendments();

		/*
		 * 각각의 amendment에 대하여
		 * Merge 로직을 가지고 있는 handler를 찾아서 실행합니다.
		 */
		amendments.forEach(amendment ->
			amendmentMergeTemplateMapper.getTemplateForType(amendment.getType())
				.handle(document, amendment)
		);

		document.incrementCurrentRevision();
	}
}
