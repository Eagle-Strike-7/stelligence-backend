package goorm.eagle7.stelligence.domain.debate.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.domain.debate.DebateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DebateScheduler {

	private final DebateRepository debateRepository;

	@Scheduled(fixedRateString = "${contribute.scheduler.scheduling-interval-ms:600000}")
	@Transactional
	public void closeTargetDebate() {

		List<Long> targetDebateIdList = debateRepository.findOpenDebateIdByEndAt(LocalDateTime.now());
		if (!targetDebateIdList.isEmpty()) {
			log.info("[DebateScheduler] 종료 대상 토론을 모두 종료합니다. 대상 토론 ID: {}", targetDebateIdList);
			debateRepository.closeDebateById(targetDebateIdList);
		} else {
			log.info("[DebateScheduler] 종료 대상 토론이 없습니다.");
		}
	}
}
