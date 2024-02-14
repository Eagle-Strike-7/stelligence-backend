package goorm.eagle7.stelligence.domain.badge.strategy;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.badge.model.Badge;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.report.CommentReportRepository;
import goorm.eagle7.stelligence.domain.report.DocumentReportRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReportBadgeAwardStrategy implements BadgeAwardStrategy {

	private final DocumentReportRepository documentReportRepository;
	private final CommentReportRepository commentReportRepository;

	@Override
	public boolean supports(BadgeCategory category) {
		return BadgeCategory.REPORT.equals(category);
	}

	@Override
	public long getCount(Member member) {
		long countDocument = documentReportRepository.countByReporterId(member.getId());
		long countComment = commentReportRepository.countByReporterId(member.getId());
		return countDocument + countComment;
	}

	@Override
	public Map<Integer, Badge> getRequiredCounts() {
		Map<Integer, Badge> requiredCounts = new HashMap<>();
		requiredCounts.put(10, Badge.GUARD);
		return requiredCounts;
	}
}
