package goorm.eagle7.stelligence.domain.badge;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.badge.template.BadgeAwardTemplate;
import goorm.eagle7.stelligence.domain.badge.template.BadgeTemplateMatcher;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BadgeService {

	private final BadgeTemplateMatcher badgeTemplateMatcher;

	/**
	 * <h2>BadgeCategory에 해당하는 전략을 찾아서 수행</h2>
	 * @param badgeCategory BadgeCategory
	 * @param member Member
	 * @throws IllegalArgumentException BadgeCategory에 해당하는 전략이 없을 경우
	 */
	public void checkAndAwardBadge(BadgeCategory badgeCategory, Member member) {

		// badgeCategory에 해당하는 전략 category 찾기
		BadgeAwardTemplate strategy = findStrategy(badgeCategory);

		// 해당 전략 조건에 해당하면 배지 발급
		awardBadge(strategy, member);

	}

	private BadgeAwardTemplate findStrategy(BadgeCategory badgeCategory) {
		BadgeAwardTemplate strategy = badgeTemplateMatcher.findStrategy(badgeCategory);
		if (strategy == null) {
			// BadgeCategory에 해당하는 전략이 없을 경우는 server error로 판단
			throw new IllegalArgumentException("Unsupported badge category: " + badgeCategory);
		}
		return strategy;
	}

	private void awardBadge(BadgeAwardTemplate strategy, Member member) {
		strategy.checkAndAward(member);
	}

}
