package goorm.eagle7.stelligence.domain.badge.template;

import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.domain.badge.model.Badge;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.member.model.Member;

public abstract class BadgeAwardTemplate {

	protected abstract boolean supports(BadgeCategory badgeCategory);

	protected abstract Map<Integer, Badge> getBadgeCriteria();

	protected abstract long getCount(Long memberId);

	/**
	 * <h2>전략 상세 조건 확인 후 발급</h2>
	 * <p>- 조건 숫자와 동일한 경우, 해당 배지를 찾아 배지 발급</p>
	 * <p>- 오버라이딩 방지를 위해 final 고려했으나, transactional과 함께 사용 불가해 선택 X</p>
	 * <p>- transactional이 더 중요하다 판단</p>
	 * @param member 해당 이벤트 확인할 member
	 */
	@Transactional
	public void checkAndAward(Member member) {

		long count = getCount(member.getId());

		getBadgeCriteria()
			.entrySet().stream()
			.filter(entry -> count == entry.getKey())
			.map(Map.Entry::getValue)
			.findAny()
			.ifPresent(member::addBadge);

	}

}
