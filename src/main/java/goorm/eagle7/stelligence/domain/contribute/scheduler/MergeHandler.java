package goorm.eagle7.stelligence.domain.contribute.scheduler;

import static org.springframework.transaction.annotation.Propagation.*;

import java.util.Comparator;

import org.springframework.cache.annotation.CacheEvict;
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
// @Transactional
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
	 * <p><b>TRANSACTION_REQUIRES_NEW</b> 이 메서드는 Scheduler로부터 호출됩니다.
	 * 이 경우 이 곳에서 발생한 Exception이 스케쥴링 오브젝트로 전달된다면
	 * 전체 스케쥴링 작업이 롤백될 가능성이 있습니다. 이는 원하지 않은 결과입니다. 각각의 수정요청에 대한 처리는
	 * 하나가 실패한다고 해서 다른 것 까지 실패하지는 않아야 합니다. 따라서 REQUIRES_NEW로 트랜잭션을 분리하여
	 * 이 곳에서 발생한 Exception이 스케쥴링 작업에 영향을 미치지 않도록 합니다.
	 *
	 * <p><b>WARNING - DB CONNECTION</b> 이 메서드가 수행되는 동안은 해당 쓰레드에서 DB 커넥션 2개를
	 * 점유하게 됩니다. 이는 사용자 경험에 악영향을 미칠 수 있습니다.
	 *
	 * <p><b>CACHE EVICTION</b> 이 메서드가 수행되면 해당 문서 내용의 캐시가 삭제됩니다.
	 *
	 * @param contribute 반영할 Contribute
	 */
	@Override
	@Transactional(propagation = REQUIRES_NEW)
	@CacheEvict(value = "document", key = "#contribute.document.id", cacheManager = "cacheManager")
	public void handle(Contribute contribute) {
		log.trace("MergeService.merge called");
		log.info("Contribute {} is merging", contribute.getId());

		Document document = contribute.getDocument();

		contribute.getAmendments()
			.stream()
			.sorted(AMENDMENT_COMPARATOR) // Status가 CREATE일 때는 targetSection의 order 기준으로 정렬
			.forEach(amendment -> {// 각각의 amendment에 대하여 Merge 로직을 가지고 있는 template를 찾아서 실행합니다.
					// amendment의 targetSections을 영속성 컨텍스트에 등록합니다.
					amendmentMergeTemplateMapper.getTemplateForType(amendment.getType()).handle(document, amendment);
				}

			);

		//문서의 현재 revision을 증가시킵니다.
		document.incrementCurrentRevision();

		//Contribute의 상태를 MERGED로 변경합니다.
		contribute.setStatusMerged();
	}
}

