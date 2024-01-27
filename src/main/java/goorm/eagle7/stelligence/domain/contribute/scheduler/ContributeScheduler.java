package goorm.eagle7.stelligence.domain.contribute.scheduler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import lombok.extern.slf4j.Slf4j;

/**
 * 투표 기간이 끝난 수정 요청을 찾아 토론을 생성하거나 병합을 수행하는 스케쥴러입니다.
 *
 * <p>스케쥴링 주기는 contribute.scheduler.scheduling-interval-ms 프로퍼티를 통해 설정할 수 있으며,
 * 기본값은 10분입니다. fixed rate 정책을 사용하여 스케쥴링 작업의 종료시간과 상관없이 고정된 시간마다 작업이 수행됩니다.
 *
 * <p> 각각의 수정요청은 {@link ContributeSchedulingActionDeterminer}를 통해 병합, 토론, 반려 중의 행동이 결정됩니다.
 * 이후 각각의 핸들러를 통해 관련 작업이 수행됩니다. 핸들러는 생성자로 받아 Map에 저장하는데, 이 과정 때문에
 * RequiredArgsConstructor 대신 생성자를 직접 작성하였습니다.
 */
@Slf4j
@Component
public class ContributeScheduler {

	private final ContributeSchedulingActionDeterminer contributeConditionChecker;
	private final Map<ContributeSchedulingAction, ContributeSchedulingActionHandler> handlers;
	private final SchedulingTargetContributeExtractor schedulingTargetContributeExtractor;

	public ContributeScheduler(
		ContributeSchedulingActionDeterminer contributeConditionChecker,
		MergeHandler mergeHandler,
		DebateHandler debateHandler,
		RejectHandler rejectHandler,
		SchedulingTargetContributeExtractor schedulingTargetContributeExtractor
	) {
		this.contributeConditionChecker = contributeConditionChecker;
		this.schedulingTargetContributeExtractor = schedulingTargetContributeExtractor;

		//매핑을 통해 ContributeSchedulingAction에 따른 핸들러를 가져올 수 있도록 한다.
		this.handlers = Map.of(
			ContributeSchedulingAction.MERGE, mergeHandler,
			ContributeSchedulingAction.DEBATE, debateHandler,
			ContributeSchedulingAction.REJECT, rejectHandler
		);
	}

	@Scheduled(fixedRateString = "${contribute.scheduler.scheduling-interval-ms:600000}")
	// 분 단위로 받아서 ms로 변환
	public void scheduleContribute() {
		log.info("ContributeScheduler가 수행됩니다.");

		// 현재 시간을 기준으로 스케쥴링의 대상이 되는 Contribute를 가져온다.
		List<Contribute> contributes = schedulingTargetContributeExtractor.extractContributes(LocalDateTime.now());
		log.debug("스케쥴링 대상 Contribute : {}", contributes.stream().map(Contribute::getId).toList());

		// 가져온 Contribute들에 대하여 병합, 토론, 반려를 수행한다.
		for (Contribute contribute : contributes) {
			ContributeSchedulingAction action = contributeConditionChecker.check(contribute);
			handlers.get(action).handle(contribute.getId());
		}
	}

}
