package goorm.eagle7.stelligence.domain.badge.event.listener;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.domain.badge.BadgeService;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.report.ReportRepository;
import goorm.eagle7.stelligence.domain.report.event.NewReportEvent;
import goorm.eagle7.stelligence.domain.report.model.Report;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReportBadgeIssuer {

	private final BadgeService badgeService;
	private final MemberRepository memberRepository;
	private final ReportRepository reportRepository;

	@Async
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@EventListener(value = NewReportEvent.class)
	public void onReportNew(NewReportEvent event) {
		reportRepository.findById(event.reportId())
			.ifPresent(this::awardBadgeToReporter);
	}

	private void awardBadgeToReporter(Report report) {
		memberRepository.findByIdAndActiveTrue(report.getReporterId())
			.ifPresent(member -> badgeService.checkAndAwardBadge(BadgeCategory.REPORT, member));
	}

}

