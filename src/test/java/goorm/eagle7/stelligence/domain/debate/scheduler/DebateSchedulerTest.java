package goorm.eagle7.stelligence.domain.debate.scheduler;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator;
import goorm.eagle7.stelligence.domain.debate.repository.DebateRepository;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.model.DebateStatus;

@ExtendWith(MockitoExtension.class)
class DebateSchedulerTest {

	@Mock
	private DebateRepository debateRepository;

	@InjectMocks
	private DebateScheduler debateScheduler;

	@Test
	@DisplayName("종료 시간이 지난 토론 닫기")
	void closeTargetDebate() {

		// given
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime tomorrow = now.plusDays(1L);
		LocalDateTime yesterday = now.minusDays(1L);

		Long openDebateId1 = 1L;
		Long openDebateId2 = 2L;
		Long openDebateId3 = 3L;
		Long openDebateId4 = 4L;

		Debate debate1 = TestFixtureGenerator.debate(openDebateId1, null, DebateStatus.OPEN, yesterday, 0);
		Debate debate2 = TestFixtureGenerator.debate(openDebateId2, null, DebateStatus.OPEN, yesterday, 0);
		Debate debate3 = TestFixtureGenerator.debate(openDebateId3, null, DebateStatus.OPEN, tomorrow, 0);
		Debate debate4 = TestFixtureGenerator.debate(openDebateId4, null, DebateStatus.OPEN, tomorrow, 0);

		List<Long> targetDebateIdList = List.of(openDebateId1, openDebateId2);
		when(debateRepository.findOpenDebateIdByEndAt(now)).thenReturn(targetDebateIdList);

		try (MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class)) {
			mockedLocalDateTime.when(LocalDateTime::now).thenReturn(now);

			// when
			debateScheduler.detectAndCloseTargetDebate();

			// then
			verify(debateRepository, times(1)).findOpenDebateIdByEndAt(now);
			verify(debateRepository, times(1)).closeAllById(targetDebateIdList);
		}
	}
}