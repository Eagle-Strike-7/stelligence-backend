package goorm.eagle7.stelligence.domain.debate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import goorm.eagle7.stelligence.domain.debate.dto.DebateOrderCondition;
import goorm.eagle7.stelligence.domain.debate.dto.DebatePageResponse;
import goorm.eagle7.stelligence.domain.debate.dto.DebateResponse;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.model.DebateStatus;
import goorm.eagle7.stelligence.domain.debate.repository.DebateRepository;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.member.model.Member;

@ExtendWith(MockitoExtension.class)
class DebateServiceTest {

	@Mock
	private DebateRepository debateRepository;

	@InjectMocks
	private DebateService debateService;

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
	@DisplayName("열려있는 토론 페이징 적용하여 최신순으로 조회")
	void getOpenDebatePageByLatest() {
		// given
		Page mockPage = mock(Page.class);
		Pageable pageable = mock(Pageable.class);
		when(debateRepository.findPageByStatusAndOrderCondition(
			DebateStatus.OPEN, DebateOrderCondition.LATEST, pageable))
			.thenReturn(mockPage);

		// when
		DebatePageResponse debatePage = debateService.getDebatePage(
			DebateStatus.OPEN, DebateOrderCondition.LATEST, pageable);

		// then
		verify(debateRepository, times(1)).findPageByStatusAndOrderCondition(
			DebateStatus.OPEN, DebateOrderCondition.LATEST, pageable);
		verify(debateRepository, never()).findPageByStatusAndOrderCondition(
			DebateStatus.OPEN, DebateOrderCondition.RECENT_COMMENTED, pageable);
		verify(debateRepository, never()).findPageByStatusAndOrderCondition(
			DebateStatus.CLOSED, DebateOrderCondition.LATEST, pageable);
		verify(debateRepository, never()).findPageByStatusAndOrderCondition(
			DebateStatus.CLOSED, DebateOrderCondition.RECENT_COMMENTED, pageable);
	}

	@Test
	@DisplayName("열려있는 토론 페이징 적용하여 최근댓글순으로 조회")
	void getOpenDebatePageByRecent() {
		// given
		Page mockPage = mock(Page.class);
		Pageable pageable = mock(Pageable.class);
		when(debateRepository.findPageByStatusAndOrderCondition(
			DebateStatus.OPEN, DebateOrderCondition.RECENT_COMMENTED, pageable))
			.thenReturn(mockPage);

		// when
		DebatePageResponse debatePage = debateService.getDebatePage(
			DebateStatus.OPEN, DebateOrderCondition.RECENT_COMMENTED, pageable);

		// then
		verify(debateRepository, never()).findPageByStatusAndOrderCondition(
			DebateStatus.OPEN, DebateOrderCondition.LATEST, pageable);
		verify(debateRepository, times(1)).findPageByStatusAndOrderCondition(
			DebateStatus.OPEN, DebateOrderCondition.RECENT_COMMENTED, pageable);
		verify(debateRepository, never()).findPageByStatusAndOrderCondition(
			DebateStatus.CLOSED, DebateOrderCondition.LATEST, pageable);
		verify(debateRepository, never()).findPageByStatusAndOrderCondition(
			DebateStatus.CLOSED, DebateOrderCondition.RECENT_COMMENTED, pageable);
	}

	@Test
	@DisplayName("닫혀있는 토론 페이징 적용하여 최신순으로 조회")
	void getClosedDebatePageByLatest() {
		// given
		Page mockPage = mock(Page.class);
		Pageable pageable = mock(Pageable.class);
		when(debateRepository.findPageByStatusAndOrderCondition(
			DebateStatus.CLOSED, DebateOrderCondition.LATEST, pageable))
			.thenReturn(mockPage);

		// when
		DebatePageResponse debatePage = debateService.getDebatePage(
			DebateStatus.CLOSED, DebateOrderCondition.LATEST, pageable);

		// then
		verify(debateRepository, never()).findPageByStatusAndOrderCondition(
			DebateStatus.OPEN, DebateOrderCondition.LATEST, pageable);
		verify(debateRepository, never()).findPageByStatusAndOrderCondition(
			DebateStatus.OPEN, DebateOrderCondition.RECENT_COMMENTED, pageable);
		verify(debateRepository, times(1)).findPageByStatusAndOrderCondition(
			DebateStatus.CLOSED, DebateOrderCondition.LATEST, pageable);
		verify(debateRepository, never()).findPageByStatusAndOrderCondition(
			DebateStatus.CLOSED, DebateOrderCondition.RECENT_COMMENTED, pageable);
	}

	@Test
	@DisplayName("닫혀있는 토론 페이징 적용하여 최근댓글순으로 조회")
	void getClosedDebatePageByRecent() {
		// given
		Page mockPage = mock(Page.class);
		Pageable pageable = mock(Pageable.class);
		when(debateRepository.findPageByStatusAndOrderCondition(
			DebateStatus.CLOSED, DebateOrderCondition.RECENT_COMMENTED, pageable))
			.thenReturn(mockPage);

		// when
		DebatePageResponse debatePage = debateService.getDebatePage(
			DebateStatus.CLOSED, DebateOrderCondition.RECENT_COMMENTED, pageable);

		// then
		verify(debateRepository, never()).findPageByStatusAndOrderCondition(
			DebateStatus.OPEN, DebateOrderCondition.LATEST, pageable);
		verify(debateRepository, never()).findPageByStatusAndOrderCondition(
			DebateStatus.OPEN, DebateOrderCondition.RECENT_COMMENTED, pageable);
		verify(debateRepository, never()).findPageByStatusAndOrderCondition(
			DebateStatus.CLOSED, DebateOrderCondition.LATEST, pageable);
		verify(debateRepository, times(1)).findPageByStatusAndOrderCondition(
			DebateStatus.CLOSED, DebateOrderCondition.RECENT_COMMENTED, pageable);
	}

}