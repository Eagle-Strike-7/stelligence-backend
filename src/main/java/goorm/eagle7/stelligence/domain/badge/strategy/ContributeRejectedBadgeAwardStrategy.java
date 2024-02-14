package goorm.eagle7.stelligence.domain.badge.strategy;

import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.badge.model.Badge;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ContributeRejectedBadgeAwardStrategy implements BadgeAwardStrategy {

    private final ContributeRepository contributeRepository;

    @Override
    public boolean supports(BadgeCategory category) {
        return BadgeCategory.CONTRIBUTE_REJECTED.equals(category);
    }

    @Override
    public Badge checkAndAward(BadgeCategory badgeCategory, Member member) {
        long count = contributeRepository.countByMemberIdAndStatus(member.getId(), ContributeStatus.REJECTED);
        return Badge.findByEventCategoryAndCount(badgeCategory, count);
    }
}
