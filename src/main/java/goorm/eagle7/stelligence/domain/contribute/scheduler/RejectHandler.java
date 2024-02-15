package goorm.eagle7.stelligence.domain.contribute.scheduler;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.event.ContributeRejectedEvent;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 투표가 종료된 Contribute를 반려 처리합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RejectHandler implements ContributeSchedulingActionHandler {

	private final ContributeRepository contributeRepository;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Override
	@Transactional
	public void handle(Long contributeId) {
		log.debug("Contribute {} is rejected.", contributeId);
		Contribute contribute = contributeRepository.findById(contributeId).orElseThrow();
		contribute.setStatusRejected();

		applicationEventPublisher.publishEvent(new ContributeRejectedEvent(contributeId));
		applicationEventPublisher.publishEvent(new ContributeRejectedEvent(contributeId));
	}
}
