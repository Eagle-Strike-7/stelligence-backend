package goorm.eagle7.stelligence.domain.badge;

import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.domain.badge.event.NewBadgeEvent;
import goorm.eagle7.stelligence.domain.badge.model.Badge;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.badge.template.BadgeMatchedCountTemplate;
import goorm.eagle7.stelligence.domain.badge.template.BadgeTemplateMatcher;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BadgeService {

	private final BadgeTemplateMatcher badgeTemplateMatcher;
	private final ApplicationEventPublisher eventPublisher;

	/**
	 * <h2>BadgeCategory에 해당하는 template을 찾아서 수행</h2>
	 * @param badgeCategory BadgeCategory
	 * @param member Member
	 * @throws IllegalArgumentException BadgeCategory에 해당하는 template이 없을 경우
	 */
	@Transactional
	public void checkAndAwardBadge(BadgeCategory badgeCategory, Member member) {

		// badgeCategory에 해당하는 전략 category 찾기
		BadgeMatchedCountTemplate template = findTemplate(badgeCategory);

		// 배지 찾기
		getBadgeMatchedMemberCondition(template, member)
			.ifPresent(
				// 배지 발급 및 배지 발급 이벤트 발행
				badge -> awardBadge(member, badge)
			);

	}

	/**
	 * <h2>BadgeCategory에 해당하는 조건 템플릿 찾기</h2>
	 * @param badgeCategory BadgeCategory
	 * @return template 반환
	 * @throws IllegalArgumentException BadgeCategory에 해당하는 template이 없을 경우
	 */
	private BadgeMatchedCountTemplate findTemplate(BadgeCategory badgeCategory) {

		BadgeMatchedCountTemplate template = badgeTemplateMatcher.findTemplate(badgeCategory);

		if (template == null) {
			// BadgeCategory에 해당하는 템플릿이 없을 경우는 server error로 판단
			throw new IllegalArgumentException("해당하는 badge category가 없습니다.: " + badgeCategory);
		}

		return template;
	}

	/**
	 * <h2>template과 해당 member의 조건에 충족하는 배지 찾기</h2>
	 * @param template 배지 발급 조건 템플릿
	 * @param member 확인할 Member
	 * @return Optional<Badge> 해당 member의 조건에 충족하는 배지
	 */
	private Optional<Badge> getBadgeMatchedMemberCondition(BadgeMatchedCountTemplate template, Member member) {
		return template.getBadgeWithCount(member);
	}

	/**
	 * <h2>배지 발급</h2>
	 * @param member Member
	 * @param badge Badge
	 */
	private void awardBadge(Member member, Badge badge) {
		member.addBadge(badge);
		eventPublisher.publishEvent(new NewBadgeEvent(member.getId(), badge));
	}

}
