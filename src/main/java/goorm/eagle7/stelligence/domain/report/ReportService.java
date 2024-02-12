package goorm.eagle7.stelligence.domain.report;

import org.springframework.stereotype.Service;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.debate.repository.CommentRepository;
import goorm.eagle7.stelligence.domain.document.content.DocumentContentRepository;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.report.dto.ReportRequest;
import goorm.eagle7.stelligence.domain.report.model.CommentReport;
import goorm.eagle7.stelligence.domain.report.model.DocumentReport;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

	private final DocumentReportRepository documentReportRepository;
	private final CommentReportRepository commentReportRepository;
	private final DocumentContentRepository documentContentRepository;
	private final CommentRepository commentRepository;
	private final MemberRepository memberRepository;

	public void reportDocument(Long documentId, ReportRequest reportRequest, Long reporterId) {
		if (!documentContentRepository.existsById(documentId)) {
			throw new BaseException("존재하지 않는 문서에 대한 신고 요청입니다. 문서 ID: " + documentId);
		}
		if (!memberRepository.existsById(reporterId)) {
			throw new BaseException("존재하지 않는 회원의 신고 요청입니다. 회원 ID: " + reporterId);
		}
		if (documentReportRepository.existsByDocumentIdAndReporterId(documentId, reporterId)) {
			throw new BaseException("이미 처리된 신고 요청입니다.");
		}

		DocumentReport documentReport = DocumentReport.createDocumentReport(
			documentId, reportRequest.getDescription(), reporterId);
		documentReportRepository.save(documentReport);
	}

	public void reportComment(Long commentId, ReportRequest reportRequest, Long reporterId) {
		if (!commentRepository.existsById(commentId)) {
			throw new BaseException("존재하지 않는 댓글에 대한 신고 요청입니다. 댓글 ID: " + commentId);
		}
		if (!memberRepository.existsById(reporterId)) {
			throw new BaseException("존재하지 않는 회원의 신고 요청입니다. 회원 ID: " + reporterId);
		}
		if (commentReportRepository.existsByCommentIdAndReporterId(commentId, reporterId)) {
			throw new BaseException("이미 처리된 신고 요청입니다.");
		}

		CommentReport commentReport = CommentReport.createCommentReport(
			commentId, reportRequest.getDescription(), reporterId);
		commentReportRepository.save(commentReport);
	}
}
