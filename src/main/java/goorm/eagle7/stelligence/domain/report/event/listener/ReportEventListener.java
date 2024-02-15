package goorm.eagle7.stelligence.domain.report.event.listener;

import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.domain.badge.BadgeService;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.report.CommentReportRepository;
import goorm.eagle7.stelligence.domain.report.DocumentReportRepository;
import goorm.eagle7.stelligence.domain.report.event.NewReportEvent;
import goorm.eagle7.stelligence.domain.report.model.Report;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReportEventListener {

	private final BadgeService badgeService;
	private final MemberRepository memberRepository;
	private final DocumentReportRepository documentReportRepository;
	private final CommentReportRepository commentReportRepository;

	@Async
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@EventListener(value = NewReportEvent.class)
	public void onReportNew(NewReportEvent event) {

		documentReportRepository.findById(event.reportId())
			.ifPresentOrElse(
				report -> awardBadgeToReporter(Optional.of(report)),
				() -> commentReportRepository.findById(event.reportId())
					.ifPresent(report -> awardBadgeToReporter(Optional.of(report)))
			);

	}

	private void awardBadgeToReporter(Optional<? extends Report> reportOptional) {
		reportOptional
			.map(Report::getReporterId)
			.flatMap(memberRepository::findByIdAndActiveTrue)
			.ifPresent(member ->
				badgeService.checkAndAwardBadge(BadgeCategory.REPORT, member));
	}

}

