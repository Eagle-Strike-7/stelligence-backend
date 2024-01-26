package goorm.eagle7.stelligence.domain.contribute.scheduler;

import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import lombok.extern.slf4j.Slf4j;

/**
 * 투표가 종료된 Contribute에 대한 토론을 생성합니다.
 */
@Slf4j
@Component
public class DebateHandler implements ContributeSchedulingActionHandler {

	@Override
	public void handle(Contribute contribute) {
		log.info("Contribute {} debate open", contribute.getId());
		contribute.setStatusDebating();
	}
}
