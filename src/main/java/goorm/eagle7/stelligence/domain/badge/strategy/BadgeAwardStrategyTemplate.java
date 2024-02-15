package goorm.eagle7.stelligence.domain.badge.strategy;

import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.domain.badge.model.Badge;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.member.model.Member;

public abstract class BadgeAwardStrategyTemplate {


	protected abstract boolean supports(BadgeCategory badgeCategory);

	protected abstract Map<Integer, Badge> getBadgeCriteria();

	protected abstract long getCount(Long memberId);

	/**
	 * <h2>전략 상세 조건 확인 후 발급</h2>
	 * <p>- 조건 숫자와 동일한 경우, 해당 배지를 찾아 배지 발급</p>
	 * @param member
	 */
	@Transactional
	public void checkAndAward(Member member) {

		long count = getCount(member.getId());

		getBadgeCriteria().entrySet().stream()
			.filter(entry -> count == entry.getKey())
			.map(Map.Entry::getValue)
			.findAny()
			.ifPresent(member::addBadge);
	}


}
