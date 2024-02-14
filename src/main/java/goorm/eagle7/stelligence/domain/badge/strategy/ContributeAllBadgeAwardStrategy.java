package goorm.eagle7.stelligence.domain.badge.strategy;

import static goorm.eagle7.stelligence.domain.badge.model.Badge.*;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.badge.model.Badge;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ContributeAllBadgeAwardStrategy implements BadgeAwardStrategy {

    private final ContributeRepository contributeRepository;

    @Override
    public boolean supports(BadgeCategory category) {
        return BadgeCategory.CONTRIBUTE_ALL.equals(category);
    }


    public long getCount(Member member) {
        return contributeRepository.countByMemberId(member.getId());
    }

    @Override
    public Map<Integer, Badge> getRequiredCounts() {
        Map<Integer, Badge> requiredCounts = new HashMap<>();
        requiredCounts.put(1, MERCURY);
        requiredCounts.put(5, VENUS);
        requiredCounts.put(10, NEPTUNE);
        requiredCounts.put(30, SUN);
        requiredCounts.put(50, GALAXY);
        return requiredCounts;
    }

}
