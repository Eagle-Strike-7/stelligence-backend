package goorm.eagle7.stelligence.domain.badge;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.domain.badge.model.Badge;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.document.content.DocumentContentRepository;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;

@ExtendWith(MockitoExtension.class)
class BadgeServiceTest {

	@Mock
	private MemberRepository memberRepository;
	@Mock
	private ContributeRepository contributeRepository;
	@Mock
	private DocumentContentRepository documentContentRepository;
	@InjectMocks
	private BadgeService badgeService;

	private Member member;

	@BeforeEach
	void setUp() {
		member = member(1L, "nickname");
	}

	@Test
	@DisplayName("[성공] - 배지 추가, 글 작성 - WRITING, 1")
	void getBadgeWriting1Success() {

		// given
		BadgeCategory badgeCategory = BadgeCategory.WRITING;
		when(documentContentRepository.countByAuthor_Id(member.getId())).thenReturn(1L);
		// Badge.findByEventCategoryAndCount(badgeCategory, count)
		// when(Badge.findByEventCategoryAndCount(any(BadgeCategory.WRITING.getDeclaringClass()), 1L)).thenReturn(Badge.ASTRONAUT); // TODO enum은 any를 해야 하는지, 아니면 enum이라 그냥 넣어도 되는지

		// when
		badgeService.checkAndAwardBadge(badgeCategory, member);

		// then
		assertThat(member.getBadges())
			.isNotEmpty()
			.contains(Badge.ASTRONAUT)
			.hasSize(1);
		verify(documentContentRepository, times(1)).countByAuthor_Id(member.getId());
		verify(contributeRepository, never()).countByMemberId(member.getId());
		verify(contributeRepository, never()).countByMemberIdAndStatus(member.getId(), ContributeStatus.MERGED);
		verify(contributeRepository, never()).countByMemberIdAndStatus(member.getId(),
			ContributeStatus.REJECTED);

	}

	@Test
	@DisplayName("[예외] - 배지 추가, 글 작성 X (repository에 저장되지 않은 상황 가정) - WRITING, 0")
	void getBadgeWriting0() {

		// given
		BadgeCategory badgeCategory = BadgeCategory.WRITING;
		when(documentContentRepository.countByAuthor_Id(member.getId())).thenReturn(0L);

		// when
		badgeService.checkAndAwardBadge(badgeCategory, member);

		// then
		assertThat(member.getBadges()).isEmpty();
		verify(documentContentRepository, times(1)).countByAuthor_Id(member.getId());
		verify(contributeRepository, never()).countByMemberId(member.getId());
		verify(contributeRepository, never()).countByMemberIdAndStatus(member.getId(), ContributeStatus.MERGED);
		verify(contributeRepository, never()).countByMemberIdAndStatus(member.getId(),
			ContributeStatus.REJECTED);

	}

	@Test
	@DisplayName("[성공] - 배지 추가, 모든 수정 요청 - CONTRIBUTE_ALL, 5")
	void getBadgeContributeAll5Success() {

		// given
		BadgeCategory badgeCategory = BadgeCategory.CONTRIBUTE_ALL;
		when(contributeRepository.countByMemberId(member.getId())).thenReturn(5L);

		// when
		badgeService.checkAndAwardBadge(badgeCategory, member);

		// then
		assertThat(member.getBadges())
			.isNotEmpty()
			.contains(Badge.VENUS)
			.hasSize(1);
		verify(contributeRepository, times(1)).countByMemberId(member.getId());
		verify(contributeRepository, never()).countByMemberIdAndStatus(member.getId(), ContributeStatus.MERGED);
		verify(contributeRepository, never()).countByMemberIdAndStatus(member.getId(),
			ContributeStatus.REJECTED);
		verify(documentContentRepository, never()).countByAuthor_Id(member.getId());

	}

	@Test
	@DisplayName("[예외] - 배지 추가, 모든 수정 요청 사잇값 - CONTRIBUTE_ALL, 8")
	void getBadgeContributeAll8() {

		// given
		BadgeCategory badgeCategory = BadgeCategory.CONTRIBUTE_ALL;
		when(contributeRepository.countByMemberId(member.getId())).thenReturn(8L);

		// when
		badgeService.checkAndAwardBadge(badgeCategory, member);

		// then
		assertThat(member.getBadges()).isEmpty();
		verify(contributeRepository, times(1)).countByMemberId(member.getId());
		verify(contributeRepository, never()).countByMemberIdAndStatus(member.getId(), ContributeStatus.MERGED);
		verify(contributeRepository, never()).countByMemberIdAndStatus(member.getId(),
			ContributeStatus.REJECTED);
		verify(documentContentRepository, never()).countByAuthor_Id(member.getId());

	}

	@Test
	@DisplayName("[성공] - 배지 추가, 반영된 수정 요청 - CONTRIBUTE_MERGED, 5")
	void getBadgeContributeMerged5Success() {

		// given
		BadgeCategory badgeCategory = BadgeCategory.CONTRIBUTE_MERGED;
		when(contributeRepository.countByMemberIdAndStatus(member.getId(), ContributeStatus.MERGED)).thenReturn(
			5L);

		// when
		badgeService.checkAndAwardBadge(badgeCategory, member);

		// then
		assertThat(member.getBadges())
			.isNotEmpty()
			.contains(Badge.SATURN)
			.hasSize(1);
		verify(contributeRepository, never()).countByMemberId(member.getId());
		verify(contributeRepository, times(1)).countByMemberIdAndStatus(member.getId(), ContributeStatus.MERGED);
		verify(contributeRepository, never()).countByMemberIdAndStatus(member.getId(),
			ContributeStatus.REJECTED);
		verify(documentContentRepository, never()).countByAuthor_Id(member.getId());

	}

	@Test
	@DisplayName("[성공] - 배지 추가, 반려된 수정 요청 - CONTRIBUTE_REJECTED, 100")
	void getBadgeContributeRejected100Success() {

		// given
		BadgeCategory badgeCategory = BadgeCategory.CONTRIBUTE_REJECTED;
		when(contributeRepository.countByMemberIdAndStatus(member.getId(),
			ContributeStatus.REJECTED)).thenReturn(100L);

		// when
		badgeService.checkAndAwardBadge(badgeCategory, member);

		// then
		assertThat(member.getBadges())
			.isNotEmpty()
			.contains(Badge.BLACKHOLE)
			.hasSize(1);
		verify(contributeRepository, never()).countByMemberId(member.getId());
		verify(contributeRepository, never()).countByMemberIdAndStatus(member.getId(), ContributeStatus.MERGED);
		verify(contributeRepository, times(1)).countByMemberIdAndStatus(member.getId(),
			ContributeStatus.REJECTED);
		verify(documentContentRepository, never()).countByAuthor_Id(member.getId());

	}

	@Test
	@DisplayName("[성공] - 배지 추가, 회원 가입 - MEMBER_JOIN, 1")
	void getBadgeMemberJoin1Success() {

		// given
		BadgeCategory badgeCategory = BadgeCategory.MEMBER_JOIN;
		when(memberRepository.existsByIdAndActiveTrueAndCreatedAtGreaterThanEqual(eq(member.getId()), any(LocalDateTime.class) )).thenReturn(true);

		// when
		badgeService.checkAndAwardBadge(badgeCategory, member);

		// then
		assertThat(member.getBadges())
			.isNotEmpty()
			.contains(Badge.SPROUT)
			.hasSize(1);
		verify(memberRepository, times(1)).existsByIdAndActiveTrueAndCreatedAtGreaterThanEqual(eq(member.getId()), any(LocalDateTime.class));
		verify(contributeRepository, never()).countByMemberId(member.getId());
		verify(contributeRepository, never()).countByMemberIdAndStatus(member.getId(), ContributeStatus.MERGED);
		verify(contributeRepository, never()).countByMemberIdAndStatus(member.getId(),
			ContributeStatus.REJECTED);
		verify(documentContentRepository, never()).countByAuthor_Id(member.getId());

	}

	@Test
	@DisplayName("[실패] - 배지 추가 X, 회원 가입 실패(DB 조회 실패) - MEMBER_JOIN, 1")
	void getBadgeMemberJoinFail() {

		// given
		BadgeCategory badgeCategory = BadgeCategory.MEMBER_JOIN;
		when(memberRepository.existsByIdAndActiveTrueAndCreatedAtGreaterThanEqual(eq(member.getId()), any(LocalDateTime.class))).thenReturn(false);

		// when
		badgeService.checkAndAwardBadge(badgeCategory, member);

		// then
		assertThat(member.getBadges())
			.isEmpty();
		verify(memberRepository, times(1)).existsByIdAndActiveTrueAndCreatedAtGreaterThanEqual(eq(member.getId()), any(LocalDateTime.class));
		verify(contributeRepository, never()).countByMemberId(member.getId());
		verify(contributeRepository, never()).countByMemberIdAndStatus(member.getId(), ContributeStatus.MERGED);
		verify(contributeRepository, never()).countByMemberIdAndStatus(member.getId(),
			ContributeStatus.REJECTED);
		verify(documentContentRepository, never()).countByAuthor_Id(member.getId());

	}

	@Test
	@DisplayName("[실패] - (현재 미구현) 배지 추가, 신고 - REPORT, 10")
	void getBadgeReport10Fail() {

		// given
		BadgeCategory badgeCategory = BadgeCategory.REPORT;

		// when
		badgeService.checkAndAwardBadge(badgeCategory, member);

		// then
		assertThat(member.getBadges()).isEmpty();
		verify(contributeRepository, never()).countByMemberId(member.getId());
		verify(contributeRepository, never()).countByMemberIdAndStatus(member.getId(), ContributeStatus.MERGED);
		verify(contributeRepository, never()).countByMemberIdAndStatus(member.getId(),
			ContributeStatus.REJECTED);
		verify(documentContentRepository, never()).countByAuthor_Id(member.getId());

	}

}