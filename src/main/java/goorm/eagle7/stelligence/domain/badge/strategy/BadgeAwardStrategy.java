package goorm.eagle7.stelligence.domain.badge.strategy;

import goorm.eagle7.stelligence.domain.badge.model.Badge;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.member.model.Member;

public interface BadgeAwardStrategy {

	boolean supports(BadgeCategory badgeCategory);
	Badge checkAndAward(BadgeCategory badgeCategory, Member member);

}
