package goorm.eagle7.stelligence.domain.badge.strategy;

import java.util.HashMap;
import java.util.Map;

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
		return BadgeCategory.DOCUMENT.equals(category);
	}

	@Override
	public long getCount(Long memberId) {
		return documentContentRepository.countByAuthor_Id(memberId);
	}

	@Override
	public Map<Integer, Badge> getRequiredCounts() {
		Map<Integer, Badge> requiredCounts = new HashMap<>();
		requiredCounts.put(1, Badge.ASTRONAUT);
		requiredCounts.put(5, Badge.MOON);
		requiredCounts.put(10, Badge.MARS);
		requiredCounts.put(20, Badge.URANUS);
		return requiredCounts;
	}

}
