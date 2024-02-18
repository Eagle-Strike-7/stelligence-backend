package goorm.eagle7.stelligence.domain.badge;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

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
	@DisplayName("[성공] - 배지 추가, 글 작성 - WRITING, 1")
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

		// then
		assertThat(member.getBadges())
			.isNotEmpty()
			.contains(Badge.ASTRONAUT)
			.hasSize(1);

	}
	//
	// @Test
	// @DisplayName("[예외] - 배지 추가, 글 작성 X (repository에 저장되지 않은 상황 가정) - WRITING, 0")
	// void getBadgeWriting0() {
	//
	// 	// given
	// 	BadgeCategory badgeCategory = BadgeCategory.DOCUMENT;
	// 	when(documentContentRepository.countByAuthor_Id(member.getId())).thenReturn(0L);
	//
	// 	// when
	// 	badgeService.checkAndAwardBadge(badgeCategory, member);
	//
	// 	// then
	// 	assertThat(member.getBadges()).isEmpty();
	// 	verify(documentContentRepository, times(1)).countByAuthor_Id(member.getId());
	// 	verify(contributeRepository, never()).countByMemberId(member.getId());
	// 	verify(contributeRepository, never()).countByMemberIdAndStatus(member.getId(), ContributeStatus.MERGED);
	// 	verify(contributeRepository, never()).countByMemberIdAndStatus(member.getId(),
	// 		ContributeStatus.REJECTED);
	//
	// }
	//
	// @Test
	// @DisplayName("[성공] - 배지 추가, 모든 수정 요청 - CONTRIBUTE_ALL, 5")
	// void getBadgeContributeAll5Success() {
	//
	// 	// given
	// 	BadgeCategory badgeCategory = BadgeCategory.CONTRIBUTE_ALL;
	// 	when(contributeRepository.countByMemberId(member.getId())).thenReturn(5L);
	//
	// 	// when
	// 	badgeService.checkAndAwardBadge(badgeCategory, member);
	//
	// 	// then
	// 	assertThat(member.getBadges())
	// 		.isNotEmpty()
	// 		.contains(Badge.VENUS)
	// 		.hasSize(1);
	// 	verify(contributeRepository, times(1)).countByMemberId(member.getId());
	// 	verify(contributeRepository, never()).countByMemberIdAndStatus(member.getId(), ContributeStatus.MERGED);
	// 	verify(contributeRepository, never()).countByMemberIdAndStatus(member.getId(),
	// 		ContributeStatus.REJECTED);
	// 	verify(documentContentRepository, never()).countByAuthor_Id(member.getId());
	//
	// }
	//
	// @Test
	// @DisplayName("[예외] - 배지 추가, 모든 수정 요청 사잇값 - CONTRIBUTE_ALL, 8")
	// void getBadgeContributeAll8() {
	//
	// 	// given
	// 	BadgeCategory badgeCategory = BadgeCategory.CONTRIBUTE_ALL;
	// 	when(contributeRepository.countByMemberId(member.getId())).thenReturn(8L);
	//
	// 	// when
	// 	badgeService.checkAndAwardBadge(badgeCategory, member);
	//
	// 	// then
	// 	assertThat(member.getBadges()).isEmpty();
	// 	verify(contributeRepository, times(1)).countByMemberId(member.getId());
	// 	verify(contributeRepository, never()).countByMemberIdAndStatus(member.getId(), ContributeStatus.MERGED);
	// 	verify(contributeRepository, never()).countByMemberIdAndStatus(member.getId(),
	// 		ContributeStatus.REJECTED);
	// 	verify(documentContentRepository, never()).countByAuthor_Id(member.getId());
	//
	// }
	//
	// @Test
	// @DisplayName("[성공] - 배지 추가, 반영된 수정 요청 - CONTRIBUTE_MERGED, 5")
	// void getBadgeContributeMerged5Success() {
	//
	// 	// given
	// 	BadgeCategory badgeCategory = BadgeCategory.CONTRIBUTE_MERGED;
	// 	when(contributeRepository.countByMemberIdAndStatus(member.getId(), ContributeStatus.MERGED)).thenReturn(
	// 		5L);
	//
	// 	// when
	// 	badgeService.checkAndAwardBadge(badgeCategory, member);
	//
	// 	// then
	// 	assertThat(member.getBadges())
	// 		.isNotEmpty()
	// 		.contains(Badge.SATURN)
	// 		.hasSize(1);
	// 	verify(contributeRepository, never()).countByMemberId(member.getId());
	// 	verify(contributeRepository, times(1)).countByMemberIdAndStatus(member.getId(), ContributeStatus.MERGED);
	// 	verify(contributeRepository, never()).countByMemberIdAndStatus(member.getId(),
	// 		ContributeStatus.REJECTED);
	// 	verify(documentContentRepository, never()).countByAuthor_Id(member.getId());
	//
	// }
	//
	// @Test
	// @DisplayName("[성공] - 배지 추가, 반려된 수정 요청 - CONTRIBUTE_REJECTED, 100")
	// void getBadgeContributeRejected100Success() {
	//
	// 	// given
	// 	BadgeCategory badgeCategory = BadgeCategory.CONTRIBUTE_REJECTED;
	// 	when(contributeRepository.countByMemberIdAndStatus(member.getId(),
	// 		ContributeStatus.REJECTED)).thenReturn(100L);
	//
	// 	// when
	// 	badgeService.checkAndAwardBadge(badgeCategory, member);
	//
	// 	// then
	// 	assertThat(member.getBadges())
	// 		.isNotEmpty()
	// 		.contains(Badge.BLACKHOLE)
	// 		.hasSize(1);
	// 	verify(contributeRepository, never()).countByMemberId(member.getId());
	// 	verify(contributeRepository, never()).countByMemberIdAndStatus(member.getId(), ContributeStatus.MERGED);
	// 	verify(contributeRepository, times(1)).countByMemberIdAndStatus(member.getId(),
	// 		ContributeStatus.REJECTED);
	// 	verify(documentContentRepository, never()).countByAuthor_Id(member.getId());
	//
	// }
	//
	// @Test
	// @DisplayName("[실패] - (현재 미구현) 배지 추가, 신고 - REPORT, 10")
	// void getBadgeReport10Fail() {
	//
	// 	// given
	// 	BadgeCategory badgeCategory = BadgeCategory.REPORT;
	//
	// 	// when
	// 	badgeService.checkAndAwardBadge(badgeCategory, member);
	//
	// 	// then
	// 	assertThat(member.getBadges()).isEmpty();
	// 	verify(contributeRepository, never()).countByMemberId(member.getId());
	// 	verify(contributeRepository, never()).countByMemberIdAndStatus(member.getId(), ContributeStatus.MERGED);
	// 	verify(contributeRepository, never()).countByMemberIdAndStatus(member.getId(),
	// 		ContributeStatus.REJECTED);
	// 	verify(documentContentRepository, never()).countByAuthor_Id(member.getId());
	//
	// }

}