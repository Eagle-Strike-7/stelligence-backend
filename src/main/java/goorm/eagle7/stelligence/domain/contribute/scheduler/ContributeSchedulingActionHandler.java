package goorm.eagle7.stelligence.domain.contribute.scheduler;

import goorm.eagle7.stelligence.domain.contribute.model.Contribute;

/**
 * ContributeSchedulingAction에 따라 적합한 행동을 취하는 핸들러의 인터페이스입니다.
 */
public interface ContributeSchedulingActionHandler {

	/**
	 * ContributeSchedulingAction에 따라 적합한 행동을 수행합니다.
	 * @param contribute 수행 대상 Contribute
	 */
	void handle(Contribute contribute);
}
