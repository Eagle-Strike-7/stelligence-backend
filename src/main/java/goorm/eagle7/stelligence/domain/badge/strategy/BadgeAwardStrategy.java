package goorm.eagle7.stelligence.domain.badge.strategy;

import java.util.Map;

import goorm.eagle7.stelligence.domain.badge.model.Badge;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.member.model.Member;

public interface BadgeAwardStrategy {

	boolean supports(BadgeCategory badgeCategory);

	Map<Integer, Badge> getRequiredCounts();

	long getCount(Long memberId);

	/**
	 * <h2>전략 상세 조건 확인 후 발급</h2>
	 * <p>- 조건 숫자와 동일한 경우, 해당 배지를 찾아 배지 발급</p>
	 * @param member
	 */
	default void checkAndAward(Member member) {

		long count = getCount(member.getId());

		getRequiredCounts().entrySet().stream()
			.filter(entry -> count == entry.getKey())
			.map(Map.Entry::getValue)
			.findAny()
			.ifPresent(member::addBadge);
	}

}
