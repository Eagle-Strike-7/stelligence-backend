package goorm.eagle7.stelligence.domain.contribute.scheduler;

import org.springframework.transaction.annotation.Transactional;

/**
 * ContributeSchedulingAction에 따라 적합한 행동을 취하는 핸들러의 인터페이스입니다.
 */
public interface ContributeSchedulingActionHandler {

	/**
	 * ContributeSchedulingAction에 따라 적합한 행동을 수행합니다.
	 *
	 * <p> <b>@Transactional</b>: Scheduler에 의해서 호출되는 handle 메서드는 각각의 동작이
	 * 다른 contribute를 수행하는 동작과는 분리된 단위로 관리되어야 합니다.
	 *
	 * <p> ex) 10개의 수정요청을 반영하는 동작들 중에 하나가 실패하더라도
	 * 나머지는 성공적으로 수행된다면 나머지는 모두 반영되어야 합니다.
	 *
	 * @param contributeId 수행 대상 Contribute의 id
	 */
	@Transactional
	void handle(Long contributeId);
}
