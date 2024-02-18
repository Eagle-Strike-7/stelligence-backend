package goorm.eagle7.stelligence.domain.badge;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import goorm.eagle7.stelligence.domain.badge.event.NewBadgeEvent;
import goorm.eagle7.stelligence.domain.badge.model.Badge;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.badge.template.BadgeMatchedCountTemplate;
import goorm.eagle7.stelligence.domain.badge.template.BadgeTemplateMatcher;
import goorm.eagle7.stelligence.domain.member.model.Member;

@ExtendWith(MockitoExtension.class)
class BadgeServiceTest {

	@Mock
	private BadgeTemplateMatcher badgeTemplateMatcher;
	@Mock
	private ApplicationEventPublisher eventPublisher;

	@InjectMocks
	private BadgeService badgeService;


	@Test
	@DisplayName("[성공] - 배지 추가, 글 작성 - DOCUMENT - checkAndAwardBadge")
	void getBadgeWriting1Success() {

		// given
		// DocumentBadgeMatchedCountTemplate template = new DocumentBadgeMatchedCountTemplate(
		// 	documentContentRepository); // TODO 이건 왜 안 될까?
		Member member = member(1L, "nickname");
		BadgeMatchedCountTemplate template = mock(BadgeMatchedCountTemplate.class);
		// badgeCategory에 해당하는 전략 category 찾기
		when(badgeTemplateMatcher.findTemplate(BadgeCategory.DOCUMENT)).thenReturn(template);
		// 배지 찾기
		when(template.getBadgeWithCount(member)).thenReturn(Optional.of(Badge.ASTRONAUT));

		// when
		badgeService.checkAndAwardBadge(BadgeCategory.DOCUMENT, member);

		// then - member에게 배지가 추가되고, 이벤트가 발행되었는지 확인
		assertThat(member.getBadges())
			.isNotEmpty()
			.contains(Badge.ASTRONAUT)
			.hasSize(1); // TODO 왜 member는 mock 안 해도 되는 걸까?
		verify(eventPublisher, times(1)).publishEvent(any(NewBadgeEvent.class));

	}


	@Test
	@DisplayName("[예외] - BadgeCategory에 해당하는 템플릿이 없을 경우, IllegalArgumentException - checkAndAwardBadge")
	void getBadgeWriting1Fail() {

		// given
		// BadgeMatchedCountTemplate template = mock(BadgeMatchedCountTemplate.class);
		Member member = member(1L, "nickname");
		// badgeCategory에 해당하는 전략 category 찾기
		when(badgeTemplateMatcher.findTemplate(BadgeCategory.CONTRIBUTE_MERGED)).thenReturn(null);

		// when, then - 해당하는 badge category가 없을 경우, IllegalArgumentException
		assertThatThrownBy(() ->
			badgeService.checkAndAwardBadge(
				BadgeCategory.CONTRIBUTE_MERGED,member))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("해당하는 badge category가 없습니다.: CONTRIBUTE_MERGED");
		verify(eventPublisher, never()).publishEvent(any(NewBadgeEvent.class));

	}


	@Test
	@DisplayName("[예외] - 현재 조건을 만족하는 배지가 없는 경우, empty - checkAndAwardBadge")
	void getBadgeWriting0Fail() {

		// given
		Member member = member(1L, "nickname");
		BadgeMatchedCountTemplate template = mock(BadgeMatchedCountTemplate.class);
		// badgeCategory에 해당하는 전략 category 찾기
		when(badgeTemplateMatcher.findTemplate(BadgeCategory.REPORT)).thenReturn(template);
		// 배지 찾기
		when(template.getBadgeWithCount(member)).thenReturn(Optional.empty());

		// when
		badgeService.checkAndAwardBadge(BadgeCategory.REPORT, member);

		// then - member에게 배지가 추가되지 않았는지, eventPublisher 호출 X 확인
		assertThat(member.getBadges()).isEmpty();
		verify(eventPublisher, never()).publishEvent(any(NewBadgeEvent.class));

	}

}