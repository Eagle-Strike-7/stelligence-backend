package goorm.eagle7.stelligence.domain.badge.template.impl;

import static goorm.eagle7.stelligence.domain.badge.model.Badge.*;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.badge.model.Badge;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.badge.template.BadgeMatchedCountTemplate;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ContributeAllBadgeMatchedCountTemplate extends BadgeMatchedCountTemplate {

	private static final Map<Integer, Badge> requiredCounts = new HashMap<>();
	private final ContributeRepository contributeRepository;

	@Override
	public boolean supports(BadgeCategory category) {
		return BadgeCategory.CONTRIBUTE_ALL.equals(category);
	}

	protected long getCount(Long memberId) {
		return contributeRepository.countByMemberId(memberId);
	}

	@Override
	protected Map<Integer, Badge> getBadgeCriteria() {

		if (requiredCounts.isEmpty()) {
			requiredCounts.put(1, MERCURY);
			requiredCounts.put(5, VENUS);
			requiredCounts.put(10, NEPTUNE);
			requiredCounts.put(30, SUN);
			requiredCounts.put(50, GALAXY);
		}
		return requiredCounts;

	}

}
