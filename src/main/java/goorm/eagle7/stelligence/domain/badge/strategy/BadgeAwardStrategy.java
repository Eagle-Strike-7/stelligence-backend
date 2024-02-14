package goorm.eagle7.stelligence.domain.badge.strategy;

import java.util.Map;

import goorm.eagle7.stelligence.domain.badge.model.Badge;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.member.model.Member;

public interface BadgeAwardStrategy {

	boolean supports(BadgeCategory badgeCategory);

	Map<Integer, Badge> getRequiredCounts();

	long getCount(Member member);

	default void checkAndAward(BadgeCategory badgeCategory, Member member) {

		long count = getCount(member);

		getRequiredCounts().entrySet().stream()
			.filter(entry -> count == entry.getKey())
			.map(Map.Entry::getValue)
			.findAny()
			.ifPresent(member::addBadge);
	}

}
