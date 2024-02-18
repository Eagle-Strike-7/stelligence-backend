package goorm.eagle7.stelligence.domain.report;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.debate.repository.CommentRepository;
import goorm.eagle7.stelligence.domain.document.content.DocumentContentRepository;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.report.dto.ReportRequest;
import goorm.eagle7.stelligence.domain.report.model.CommentReport;
import goorm.eagle7.stelligence.domain.report.model.DocumentReport;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

	@Mock
	private DocumentReportRepository documentReportRepository;
	@Mock
	private CommentReportRepository commentReportRepository;
	@Mock
	private DocumentContentRepository documentContentRepository;
	@Mock
	private CommentRepository commentRepository;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private ApplicationEventPublisher eventPublisher;

	@InjectMocks
	private ReportService reportService;

	@Test
	@DisplayName("문서 신고 성공")
	void reportDocument() {
		//given
		ReportRequest reportRequest = ReportRequest.from("문서를 장난으로 작성했습니다");
		Long documentId = 1L;
		Long reporterId = 2L;


		when(documentContentRepository.existsById(documentId)).thenReturn(true);
		when(memberRepository.existsById(reporterId)).thenReturn(true);

		//when
		reportService.reportDocument(documentId, reportRequest, reporterId);

		//then
		verify(documentReportRepository, times(1)).save(any(DocumentReport.class));
	}

	@Test
	@DisplayName("문서 중복 신고")
	void reportDocumentDuplicate() {
		//given
		ReportRequest reportRequest = ReportRequest.from("문서를 장난으로 작성했습니다");
		Long documentId = 1L;
		Long reporterId = 2L;


		when(documentContentRepository.existsById(documentId)).thenReturn(true);
		when(memberRepository.existsById(reporterId)).thenReturn(true);
		when(documentReportRepository.existsByDocumentIdAndReporterId(documentId, reporterId))
			.thenReturn(true);

		//when

		//then
		assertThatThrownBy(() -> reportService.reportDocument(documentId, reportRequest, reporterId))
			.isInstanceOf(BaseException.class)
			.hasMessage("이미 처리된 신고 요청입니다.");
		verify(documentReportRepository, never()).save(any(DocumentReport.class));
	}

	@Test
	@DisplayName("토론 댓글 신고 성공")
	void reportComment() {
		//given
		ReportRequest reportRequest = ReportRequest.from("댓글에 욕설이 포함되어 있습니다");
		Long commentId = 1L;
		Long reporterId = 2L;

		when(commentRepository.existsById(commentId)).thenReturn(true);
		when(memberRepository.existsById(reporterId)).thenReturn(true);

		//when
		reportService.reportComment(commentId, reportRequest, reporterId);

		//then
		verify(commentReportRepository, times(1)).save(any(CommentReport.class));
	}

	@Test
	@DisplayName("토론 댓글 중복 신고")
	void reportCommentDuplicate() {
		//given
		ReportRequest reportRequest = ReportRequest.from("댓글에 욕설이 포함되어 있습니다");
		Long commentId = 1L;
		Long reporterId = 2L;


		when(commentRepository.existsById(commentId)).thenReturn(true);
		when(memberRepository.existsById(reporterId)).thenReturn(true);
		when(commentReportRepository.existsByCommentIdAndReporterId(commentId, reporterId))
			.thenReturn(true);

		//when

		//then
		assertThatThrownBy(() -> reportService.reportComment(commentId, reportRequest, reporterId))
			.isInstanceOf(BaseException.class)
			.hasMessage("이미 처리된 신고 요청입니다.");
		verify(commentReportRepository, never()).save(any(CommentReport.class));
	}
}