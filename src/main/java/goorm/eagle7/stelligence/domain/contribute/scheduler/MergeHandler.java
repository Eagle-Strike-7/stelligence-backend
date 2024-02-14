package goorm.eagle7.stelligence.domain.contribute.scheduler;

import java.util.Comparator;
import java.util.Objects;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.amendment.model.AmendmentType;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.event.ContributeMergedEvent;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.scheduler.template.AmendmentMergeTemplateMapper;
import goorm.eagle7.stelligence.domain.document.DocumentService;
import goorm.eagle7.stelligence.domain.document.content.DocumentContentService;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 투표가 종료된 Amendment들을 원본에 반영하기 위한 핸들러입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MergeHandler implements ContributeSchedulingActionHandler {

	private final AmendmentMergeTemplateMapper amendmentMergeTemplateMapper;
	private final ContributeRepository contributeRepository;
	private final CacheManager cacheManager;
	private final DocumentService documentService;
	private final ApplicationEventPublisher applicationEventPublisher;

	/**
	 * Amendment의 정렬은 Merge 과정에서 중요합니다. 정렬이 제대로 되지 않으면
	 * 변경 요구사항을 온전히 반영하지 못하게 됩니다.
	 *
	 * <p> 정렬은 다음과 같은 순서로 수행됩니다.
	 * <ol>
	 *    <li> Amendment의 Type이 CREATE라면 다른 타입보다 우선순위가 높습니다.</li>
	 *    <li> Amendment의 Type이 CREATE인 것 간에는 targetSection의 order 기준으로 정렬합니다.</li>
	 *    <li> targetSection이 같은 경우 creatingOrder 기준으로 정렬합니다.</li>
	 * </ol>
	 */
	private static final Comparator<Amendment> AMENDMENT_COMPARATOR = Comparator.comparing(
			(Amendment a) -> !a.getType().equals(AmendmentType.CREATE)) // CREATE 타입이 나머지 타입보다 정렬에서 우선순위
		.thenComparing(a -> a.getType().equals(AmendmentType.CREATE) ? a.getTargetSection().getOrder() :
			Integer.MAX_VALUE) // Status가 CREATE인 경우 targetSection의 order 기준으로 정렬
		.thenComparing(Amendment::getCreatingOrder); // targetSection의 order가 같은 경우 경우 creatingOrder 기준으로 정렬

	/**
	 * Contribute의 Amendment들을 원본에 반영합니다.
	 *
	 * <p><b>CACHE EVICTION</b> 이 메서드가 수행되면 해당 문서 내용의 캐시가 삭제됩니다.
	 * 기존에는 @CacheEvict 애노테이션을 사용했으나, 현재는 파라미터인 contributeId만으로 삭제할 캐시의
	 * 고유 ID를 알 수 없어서 CacheManager를 통해 직접 삭제하도록 변경하였습니다.
	 *
	 * @see DocumentContentService#getDocument(Long) 문서의 캐시가 생성되는 메서드
	 * @param contributeId 반영할 Contribute의 ID
	 */
	@Override
	@Transactional
	public void handle(Long contributeId) {
		log.info("Contribute {} is merging", contributeId);

		//Contribute를 가져옵니다.
		log.trace("contribute를 가져옵니다.");
		Contribute contribute = contributeRepository.findByIdWithAmendmentsAndMember(contributeId).orElseThrow();

		log.trace("contribute의 document를 가져옵니다.");
		Document document = contribute.getDocument();

		log.trace("contribute를 순회하며 정렬 후 Merge를 수행합니다.");
		contribute.getAmendments()
			.stream()
			.sorted(AMENDMENT_COMPARATOR) //정렬
			// 각각의 amendment에 대하여 Merge 로직을 가지고 있는 template를 찾아서 실행합니다.
			.forEach(amendment -> amendmentMergeTemplateMapper.getTemplateForType(amendment.getType())
				.handle(document, amendment)
			);

		//Document의 제목을 변경합니다.
		if (!contribute.getAfterDocumentTitle().equals(contribute.getBeforeDocumentTitle())) {
			documentService.changeDocumentTitle(document.getId(), contribute.getAfterDocumentTitle());
		}

		// Document의 부모 문서를 변경합니다.
		// 두 경우 모두 null일 수 있는 가능성을 고려합니다.
		// 부모 문서가 동일한 경우에는 업데이트하지 않습니다.
		Long beforeParentDocumentId =
			contribute.getBeforeParentDocument() == null ? null : contribute.getBeforeParentDocument().getId();
		Long afterParentDocumentId =
			contribute.getAfterParentDocument() == null ? null : contribute.getAfterParentDocument().getId();
		if (!Objects.equals(beforeParentDocumentId, afterParentDocumentId)) {
			documentService.changeParentDocument(document.getId(), afterParentDocumentId);
		}

		//문서의 현재 revision을 증가시킵니다.
		document.incrementLatestRevision();

		//Contribute의 상태를 MERGED로 변경합니다.
		contribute.setStatusMerged();

		//cache를 삭제합니다.
		// evictCache(document.getId());

		//이벤트를 발행합니다.
		applicationEventPublisher.publishEvent(new ContributeMergedEvent(contribute.getId()));
	}

	/**
	 * 해당 문서의 캐시를 삭제합니다.
	 * @param documentId 캐시를 삭제할 문서의 ID
	 */
	private void evictCache(Long documentId) {
		Cache cache = cacheManager.getCache("document");
		if (cache != null) {
			cache.evict(documentId);
			log.debug("document cache evicted. documentId : {}", documentId);
		} else {
			log.debug("document cache not found. documentId : {}", documentId);
		}
	}
}

