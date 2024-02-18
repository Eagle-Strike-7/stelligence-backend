package goorm.eagle7.stelligence.domain.badge.template;

import java.util.Map;
import java.util.Optional;

import goorm.eagle7.stelligence.domain.badge.model.Badge;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.member.model.Member;

public abstract class BadgeMatchedCountTemplate {

	protected abstract boolean supports(BadgeCategory badgeCategory);

	/**
	 * <h2>배지 조건</h2>
	 * <p>- 조건 숫자와 동일한 경우, 해당 배지를 찾아 배지 발급</p>
	 * @return 조건 숫자와 배지 매핑
	 */
	protected abstract Map<Integer, Badge> getBadgeCriteria();

	/**
	 * <h2>조건을 충족하는지 판단할 기준 count 출처 정의 및 해당 COUNT 반환</h2>
	 * @param memberId 해당 member id
	 * @return 해당 member의 count
	 */
	protected abstract long getCount(Long memberId);

	/**
	 * <h2>전략 상세 조건 확인 후, 해당 배지 반환</h2>
	 * <p>- 조건 숫자와 동일한 경우, 해당 배지를 찾아 배지 발급</p>
	 * @param member 해당 이벤트 확인할 member
	 * @return Optional<Badge> 해당 member에게 발급된 배지, 발급되지 않은 경우 empty
	 */
	public final Optional<Badge> getBadgeWithCount(Member member) {

		// member의 count 조회
		long count = getCount(member.getId());

		// 조건에 맞는 배지 찾기
		return getBadgeMatchedCount(count);

	}

	private Optional<Badge> getBadgeMatchedCount(long count) {
		return getBadgeCriteria()
			.entrySet().stream()
			.filter(entry -> count == entry.getKey())
			.map(Map.Entry::getValue)
			.findAny();
	}

}
