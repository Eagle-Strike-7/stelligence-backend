package goorm.eagle7.stelligence.domain.contribute.scheduler;

import java.util.Comparator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.amendment.model.AmendmentType;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.scheduler.template.AmendmentMergeTemplateMapper;
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
	 *
	 * @param contributeId 반영할 Contribute의 ID
	 */
	@Override
	@Transactional
	// @CacheEvict(value = "document", key = "#contribute.document.id", cacheManager = "cacheManager")
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

		//문서의 현재 revision을 증가시킵니다.
		document.incrementCurrentRevision();

		//Contribute의 상태를 MERGED로 변경합니다.
		contribute.setStatusMerged();
	}
}

