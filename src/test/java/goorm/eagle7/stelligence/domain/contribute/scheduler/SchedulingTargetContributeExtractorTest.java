package goorm.eagle7.stelligence.domain.contribute.scheduler;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;

@ExtendWith(MockitoExtension.class)
class SchedulingTargetContributeExtractorTest {

	private ContributeRepository contributeRepository;
	private SchedulingTargetContributeExtractor extractor;

	@Test
	void test() {
		contributeRepository = Mockito.mock(ContributeRepository.class);
		extractor = new SchedulingTargetContributeExtractor(contributeRepository, 10 * 60000, 2);

		//given
		// 2024년 3월 21일 10시 10분 10초에 스케쥴링이 동작한다고 가정
		LocalDateTime now = LocalDateTime.of(2024, 3, 21, 10, 10, 10);
		LocalDateTime from = now.minusMinutes(Contribute.VOTE_DURATION_MINUTE)
			.minusSeconds(10 * 60000 / 1000)
			.minusMinutes(2L);
		LocalDateTime to = now.minusMinutes(Contribute.VOTE_DURATION_MINUTE);

		//when
		extractor.extractContributes(now);

		//then
		// 2024년 3월 21일 8시 58분 10초 ~ 2024년 3월 21일 9시 10분 10초 사이에 생성된 Contribute를 가져와야한다.
		verify(contributeRepository).findByStatusIsVotingAndCreatedAtBetween(from, to);
	}
}