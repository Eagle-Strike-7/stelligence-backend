package goorm.eagle7.stelligence.domain.contribute.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import lombok.extern.slf4j.Slf4j;

/**
 * SchedulingTargetContributeExtractor
 * ContributeScheduler가 실행될 때, 실행 대상이 되는 Contribute를 가져오는 역할을 한다.
 */
@Slf4j
@Component
public class SchedulingTargetContributeExtractor {

	private final ContributeRepository contributeRepository;

	// 투표 만료 시간
	private final long voteExpirationMinutes;

	// 스케쥴링 간격
	private final long schedulingIntervalSeconds;

	// 스케쥴링 중복 시간
	private final long overlapMinutes;

	public SchedulingTargetContributeExtractor(
		ContributeRepository contributeRepository,
		@Value("${contribute.scheduler.vote-expiration-minutes:30}")
		long voteExpirationMinutes,
		@Value("${contribute.scheduler.scheduling-interval-ms:600000}")
		long schedulingIntervalMilliSeconds,
		@Value("${contribute.scheduler.overlap-minutes:5}")
		long overlapMinutes
	) {
		this.contributeRepository = contributeRepository;
		this.voteExpirationMinutes = voteExpirationMinutes;
		this.schedulingIntervalSeconds = schedulingIntervalMilliSeconds / 1000;
		this.overlapMinutes = overlapMinutes;
	}

	/**
	 * Contribute 중 파라미터로 받은 시간을 기준으로 스케쥴링의 대상이 되는 Contribute를 가져온다.
	 * 스케쥴링의 대상은 다음과 같이 결정한다.
	 *
	 * ex)
	 * 투표 기간이 1시간이고 스케쥴링 간격이 10분이라고 할 때, 현재 시각이 10:00이라면
	 * 8:50 ~ 9:00 사이에 생성된 Contribute를 가져와 처리한다.
	 *
	 * * from 의 시간에 overlap time 만큼을 더 빼주는 이유
	 * - 스케쥴링이 완벽하게 재시간에 동작한다는 보장이 안되기 때문에,
	 * - 상황에 따라서 누락되는 Contribute가 있을 수 있다.
	 *
	 * ex) 10:00:00에 스케쥴링이 동작하고, 10:10:00 에 스케쥴링이 동작한다고 가정하자.
	 * 첫 스케쥴링 에는 8:50:00 ~ 9:00:00 사이에 생성된 Contribute를 가져왔다.
	 * 하지만 두번째 스케쥴링은 서버 상황으로 인한 지연에 의해서 10:10:10 에 쿼리를 실행한다.
	 * 이 경우 9:00:10 ~ 9:10:10 사이에 생성된 Contribute를 가져오게 된다.
	 * 따라서 9:00:10 ~ 9:10:10 사이에 생성된 Contribute들은 누락된다.
	 *
	 * 이러한 문제를 해결하기 위한 방법은 여러가지가 있다.
	 *
	 * 1. 시간 범위를 오버랩시킨다: 시간 범위를 중복되게 만들어 간극을 커버하기
	 * 2. 상태 기록: 별도의 공간에 발생한 쿼리의 시간 정보를 저장하여 간극 없애기
	 *
	 * 1번을 채택하기로 하고,
	 * from 의 시간에 overlapMinutes 만큼을 더 빼주어 누락된 Contribute를 가져올 수 있도록 한다.
	 *
	 * @param now 현재 시간을 파라미터로 받을 것을 권장
	 * @return 스케쥴링의 대상이 되는 Contribute
	 */
	List<Contribute> extractContributes(LocalDateTime now) {

		LocalDateTime from = now
			.minusMinutes(voteExpirationMinutes)
			.minusSeconds(schedulingIntervalSeconds)
			.minusMinutes(overlapMinutes);

		LocalDateTime to = now.minusMinutes(voteExpirationMinutes);

		log.debug("스케쥴링 대상 Contribute 추출 쿼리 실행 : {} ~ {}", from, to);
		return contributeRepository.findByStatusIsVotingAndCreatedAtBetween(from, to);
	}

}
