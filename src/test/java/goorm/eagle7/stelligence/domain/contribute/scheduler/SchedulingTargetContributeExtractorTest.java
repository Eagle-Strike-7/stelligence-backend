package goorm.eagle7.stelligence.domain.contribute.scheduler;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;

@ExtendWith(MockitoExtension.class)
class SchedulingTargetContributeExtractorTest {

	private ContributeRepository contributeRepository;
	private SchedulingTargetContributeExtractor extractor;

	@Test
	void test() {
		contributeRepository = Mockito.mock(ContributeRepository.class);
		extractor = new SchedulingTargetContributeExtractor(contributeRepository, 60, 10 * 60000, 2);

		//given
		// 2024년 3월 21일 10시 10분 10초에 스케쥴링이 동작한다고 가정
		LocalDateTime now = LocalDateTime.of(2024, 3, 21, 10, 10, 10);

		//when
		extractor.extractContributes(now);

		//then
		// 2024년 3월 21일 8시 58분 10초 ~ 2024년 3월 21일 9시 10분 10초 사이에 생성된 Contribute를 가져와야한다.
		verify(contributeRepository).findByStatusIsVotingAndCreatedAtBetween(
			LocalDateTime.of(2024, 3, 21, 8, 58, 10),
			LocalDateTime.of(2024, 3, 21, 9, 10, 10)
		);
	}
}