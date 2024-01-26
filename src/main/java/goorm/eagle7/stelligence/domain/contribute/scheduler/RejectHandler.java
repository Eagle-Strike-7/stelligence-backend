package goorm.eagle7.stelligence.domain.contribute.scheduler;

import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import lombok.extern.slf4j.Slf4j;

/**
 * RejectHandler
 * 투표가 종료된 Contribute를 반려 처리합니다.
 */
@Slf4j
@Component
public class RejectHandler implements ContributeSchedulingActionHandler {

	@Override
	public void handle(Contribute contribute) {
		log.debug("Contribute {} is rejected.", contribute.getId());
		contribute.setStatusRejected();
	}
}
