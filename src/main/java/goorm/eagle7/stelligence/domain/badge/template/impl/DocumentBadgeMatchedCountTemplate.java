package goorm.eagle7.stelligence.domain.badge.template.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.badge.model.Badge;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.badge.template.BadgeMatchedCountTemplate;
import goorm.eagle7.stelligence.domain.document.content.DocumentContentRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DocumentBadgeMatchedCountTemplate extends BadgeMatchedCountTemplate {

	private static final Map<Integer, Badge> requiredCounts = new HashMap<>();
	private final DocumentContentRepository documentContentRepository;

	@Override
	public boolean supports(BadgeCategory category) {
		return BadgeCategory.DOCUMENT.equals(category);
	}

	@Override
	protected long getCount(Long memberId) {
		return documentContentRepository.countByAuthor_Id(memberId);
	}

	@Override
	protected Map<Integer, Badge> getBadgeCriteria() {

		if (requiredCounts.isEmpty()) {
			requiredCounts.put(1, Badge.ASTRONAUT);
			requiredCounts.put(5, Badge.MOON);
			requiredCounts.put(10, Badge.MARS);
			requiredCounts.put(20, Badge.URANUS);
		}
		return requiredCounts;
	}

}
