package goorm.eagle7.stelligence.domain.debate.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.domain.debate.event.DebateEndEvent;
import goorm.eagle7.stelligence.domain.debate.repository.DebateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 토론을 주기적으로 닫을 수 있도록 돕는 스케쥴링이 정의된 스케쥴러 클래스입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DebateScheduler {

	private final DebateRepository debateRepository;
	private final ApplicationEventPublisher applicationEventPublisher;

	/**
	 * 열려있는 토론 중, 종료 예상시간이 지난 토론을 식별하고,
	 * 벌크성 수정 쿼리를 활용해 CLOSED 상태로 전환합니다.
	 */
	@Scheduled(fixedRateString = "${contribute.scheduler.scheduling-interval-ms:600000}")
	@Transactional
	public void detectAndCloseTargetDebate() {

		List<Long> targetDebateIdList = debateRepository.findOpenDebateIdByEndAt(LocalDateTime.now());
		if (!targetDebateIdList.isEmpty()) {
			log.info("[DebateScheduler] 종료 대상 토론을 모두 종료합니다. 대상 토론 ID: {}", targetDebateIdList);
			debateRepository.closeAllById(targetDebateIdList);

			// 토론 종료 이벤트를 발행합니다.
			targetDebateIdList.forEach(
				(id) -> applicationEventPublisher.publishEvent(new DebateEndEvent(id))); //이 때 이벤트는 동기적으로 수행됩니다.

		} else {
			log.info("[DebateScheduler] 종료 대상 토론이 없습니다.");
		}
	}
}
