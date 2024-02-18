package goorm.eagle7.stelligence.domain.badge.template.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.badge.model.Badge;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.badge.template.BadgeMatchedCountTemplate;
import goorm.eagle7.stelligence.domain.report.CommentReportRepository;
import goorm.eagle7.stelligence.domain.report.DocumentReportRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReportBadgeMatchedCountTemplate extends BadgeMatchedCountTemplate {

	private static final Map<Integer, Badge> requiredCounts = new HashMap<>();
	private final DocumentReportRepository documentReportRepository;
	private final CommentReportRepository commentReportRepository;

	@Override
	public boolean supports(BadgeCategory category) {
		return BadgeCategory.REPORT.equals(category);
	}

	@Override
	protected long getCount(Long memberId) {
		long countDocument = documentReportRepository.countByReporterId(memberId);
		long countComment = commentReportRepository.countByReporterId(memberId);
		return countDocument + countComment;
	}

	@Override
	protected Map<Integer, Badge> getBadgeCriteria() {

		if (requiredCounts.isEmpty()) {
			requiredCounts.put(10, Badge.GUARD);
		}
		return requiredCounts;
	}
}
