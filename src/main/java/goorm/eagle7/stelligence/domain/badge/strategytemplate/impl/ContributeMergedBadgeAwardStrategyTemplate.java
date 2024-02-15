package goorm.eagle7.stelligence.domain.badge.strategytemplate.impl;

import static goorm.eagle7.stelligence.domain.badge.model.Badge.*;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.badge.model.Badge;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.badge.strategytemplate.BadgeAwardStrategyTemplate;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ContributeMergedBadgeAwardStrategyTemplate extends BadgeAwardStrategyTemplate {

	private static final Map<Integer, Badge> requiredCounts = new HashMap<>();
	private final ContributeRepository contributeRepository;

	@Override
	public boolean supports(BadgeCategory category) {
		return BadgeCategory.CONTRIBUTE_MERGED.equals(category);
	}

	@Override
	protected long getCount(Long memberId) {
		return contributeRepository.countByMemberIdAndStatus(memberId, ContributeStatus.MERGED);
	}

	@Override
	protected Map<Integer, Badge> getBadgeCriteria() {

		if (requiredCounts.isEmpty()) {
			requiredCounts.put(1, JUPITER);
			requiredCounts.put(5, SATURN);
			requiredCounts.put(10, PLUTO);
			requiredCounts.put(50, ANDROMEDA);
		}
		return requiredCounts;
	}

}
