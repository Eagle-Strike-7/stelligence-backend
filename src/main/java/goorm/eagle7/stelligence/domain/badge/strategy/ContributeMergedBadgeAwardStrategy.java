package goorm.eagle7.stelligence.domain.badge.strategy;

import static goorm.eagle7.stelligence.domain.badge.model.Badge.*;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.badge.model.Badge;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ContributeMergedBadgeAwardStrategy implements BadgeAwardStrategy {

	private final ContributeRepository contributeRepository;

	@Override
	public boolean supports(BadgeCategory category) {
		return BadgeCategory.CONTRIBUTE_MERGED.equals(category);
	}

	@Override
	public long getCount(Long memberId) {
		return contributeRepository.countByMemberIdAndStatus(memberId, ContributeStatus.MERGED);
	}

	@Override
	public Map<Integer, Badge> getRequiredCounts() {
		Map<Integer, Badge> requiredCounts = new HashMap<>();
		requiredCounts.put(1, JUPITER);
		requiredCounts.put(5, SATURN);
		requiredCounts.put(10, PLUTO);
		requiredCounts.put(50, ANDROMEDA);
		return requiredCounts;
	}

}
