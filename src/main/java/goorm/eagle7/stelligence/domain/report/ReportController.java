package goorm.eagle7.stelligence.domain.report;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.common.auth.memberinfo.Auth;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
import goorm.eagle7.stelligence.domain.report.dto.ReportRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {

	private final ReportService reportService;

	@PostMapping("/documents/{documentId}")
	public ResponseTemplate<Void> reportDocument(
		@RequestBody ReportRequest reportRequest,
		@PathVariable(name = "documentId") Long documentId,
		@Auth MemberInfo memberInfo) {

		reportService.reportDocument(documentId, reportRequest, memberInfo.getId());
		return ResponseTemplate.ok();
	}

	@PostMapping("/comments/{commentId}")
	public ResponseTemplate<Void> reportComment(
		@RequestBody ReportRequest reportRequest,
		@PathVariable(name = "commentId") Long commentId,
		@Auth MemberInfo memberInfo) {

		reportService.reportComment(commentId, reportRequest, memberInfo.getId());
		return ResponseTemplate.ok();
	}
}
