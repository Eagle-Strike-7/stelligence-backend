package goorm.eagle7.stelligence.domain.badge.strategy;

import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.badge.model.Badge;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.document.content.DocumentContentRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WritingBadgeAwardStrategy implements BadgeAwardStrategy {

	private final DocumentContentRepository documentContentRepository;

	@Override
	public boolean supports(BadgeCategory category) {
		return BadgeCategory.WRITING.equals(category);
	}

	@Override
	public Badge checkAndAward(BadgeCategory badgeCategory, Member member) {
		long count = documentContentRepository.countByAuthor_Id(member.getId());
		return Badge.findByEventCategoryAndCount(badgeCategory, count);
	}

}
