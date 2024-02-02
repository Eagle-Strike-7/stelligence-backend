package goorm.eagle7.stelligence.domain.debate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.debate.dto.DebatePageResponse;
import goorm.eagle7.stelligence.domain.debate.dto.DebateResponse;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.model.DebateStatus;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.member.model.Member;

@ExtendWith(MockitoExtension.class)
class DebateServiceTest {

	@Mock
	private DebateRepository debateRepository;

	@InjectMocks
	private DebateService debateService;

	@Test
	@DisplayName("열려있던 토론 종료하기")
	void closeOpenDebateById() {
		// given
		Long debateId = 1L;
		Debate debate = TestFixtureGenerator.debate(debateId, null, DebateStatus.OPEN, null, 0);
		when(debateRepository.findById(debateId)).thenReturn(Optional.of(debate));

		// when
		debateService.closeDebateById(debateId);

		// then
		// 변환하고 나면 debate의 상태가 닫힘으로 바뀌고 닫힌 시간이 현재 시간이 되어야한다.
		assertThat(debate.getStatus()).isEqualTo(DebateStatus.CLOSED);
		assertThat(debate.getEndAt()).isCloseTo(LocalDateTime.now(), within(10, ChronoUnit.SECONDS));
		// 토론은 debateRepository의 findById 메서드에 의해 찾아와진다.
		verify(debateRepository, times(1)).findById(debateId);
	}

	@Test
	@DisplayName("닫혀있던 토론 종료하기")
	void closeClosedDebateById() {
		// given
		Long debateId = 1L;
		LocalDateTime endAt = LocalDateTime.now().minusDays(1L);
		Debate debate = TestFixtureGenerator.debate(debateId, null, DebateStatus.CLOSED, endAt, 0);
		when(debateRepository.findById(debateId)).thenReturn(Optional.of(debate));

		// when
		debateService.closeDebateById(debateId);

		// then
		// 이미 닫힌 debate는 그대로 닫힘으로 유지하고, 종료 시각은 원래 상태를 유지해야한다.
		assertThat(debate.getStatus()).isEqualTo(DebateStatus.CLOSED);
		assertThat(debate.getEndAt()).isEqualTo(endAt);
		// 토론은 debateRepository의 findById 메서드에 의해 찾아와진다.
		verify(debateRepository, times(1)).findById(debateId);
	}

	@Test
	@DisplayName("토론 상세 조회")
	void getDebateDetailById() {
		// given
		Long debateId = 1L;
		Member author = TestFixtureGenerator.member(1L, "author1");
		Member contributor = TestFixtureGenerator.member(1L, "contributor1");
		Document document = TestFixtureGenerator.document(1L, author, "title1", 1L);
		Contribute contribute = TestFixtureGenerator.contribute(1L, contributor, ContributeStatus.VOTING, document);
		Debate debate = TestFixtureGenerator.debate(debateId, contribute, DebateStatus.OPEN, null, 0);

		when(debateRepository.findByIdWithContribute(debateId)).thenReturn(Optional.of(debate));

		// when
		DebateResponse debateDetail = debateService.getDebateDetailById(debateId);

		// then
		verify(debateRepository, times(1)).findByIdWithContribute(debateId);
	}

	@Test
	@DisplayName("존재하지 않는 토론 상세 조회")
	void getNonExistDebateDetailById() {
		// given
		Long debateId = 1L;

		when(debateRepository.findByIdWithContribute(debateId))
			.thenThrow(new BaseException("존재하지 않는 토론에 대한 조회 요청입니다. Debate ID: " + debateId));

		// when

		// then
		assertThatThrownBy(() -> debateService.getDebateDetailById(debateId))
			.isInstanceOf(BaseException.class)
			.hasMessage("존재하지 않는 토론에 대한 조회 요청입니다. Debate ID: " + debateId);
		verify(debateRepository, times(1)).findByIdWithContribute(debateId);
	}

	@Test
	@DisplayName("열려있는 토론 페이징 적용하여 조회")
	void getOpenDebatePage() {
		// given
		Page mockPage = mock(Page.class);
		Pageable pageable = mock(Pageable.class);
		when(debateRepository.findPageByStatus(DebateStatus.OPEN, pageable)).thenReturn(mockPage);
		
		// when
		DebatePageResponse debatePage = debateService.getDebatePage(DebateStatus.OPEN, pageable);

		// then
		verify(debateRepository, times(1)).findPageByStatus(DebateStatus.OPEN, pageable);
		verify(debateRepository, never()).findPageByStatus(DebateStatus.CLOSED, pageable);
	}

	@Test
	@DisplayName("닫혀있는 토론 페이징 적용하여 조회")
	void getClosedDebatePage() {
		// given
		Page mockPage = mock(Page.class);
		Pageable pageable = mock(Pageable.class);
		when(debateRepository.findPageByStatus(DebateStatus.CLOSED, pageable)).thenReturn(mockPage);

		// when
		DebatePageResponse debatePage = debateService.getDebatePage(DebateStatus.CLOSED, pageable);

		// then
		verify(debateRepository, times(1)).findPageByStatus(DebateStatus.CLOSED, pageable);
		verify(debateRepository, never()).findPageByStatus(DebateStatus.OPEN, pageable);
	}

}