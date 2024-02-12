package goorm.eagle7.stelligence.domain.report.model;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.domain.debate.repository.CommentRepository;
import goorm.eagle7.stelligence.domain.document.content.DocumentContentRepository;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.report.CommentReportRepository;
import goorm.eagle7.stelligence.domain.report.DocumentReportRepository;
import goorm.eagle7.stelligence.domain.report.dto.ReportRequest;

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

	@InjectMocks
	private ReportService reportService;

	@Test
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
}