package goorm.eagle7.stelligence.domain.report.event.listener;

import org.springframework.context.event.EventListener;
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

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@EventListener(value = NewReportEvent.class)
	public void onReportNew(NewReportEvent event) {

		if (documentReportRepository.existsById(event.reportId())) {
			documentReportRepository.findById(event.reportId())
				.map(Report::getReporterId)
				.ifPresent(memberId
					-> memberRepository
					.findByIdAndActiveTrue(memberId)
					.ifPresent(member ->
						badgeService.checkAndAwardBadge(BadgeCategory.REPORT, member)
					));

		} else if (commentReportRepository.existsById(event.reportId())) {
			commentReportRepository.findById(event.reportId())
				.map(Report::getReporterId)
				.ifPresent(memberId
					-> memberRepository
					.findByIdAndActiveTrue(memberId)
					.ifPresent(member ->
						badgeService.checkAndAwardBadge(BadgeCategory.REPORT, member)
					));

		}
	}

}

