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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {

	private final ReportService reportService;

	@Operation(summary = "문서 신고", description = "특정 문서를 신고합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "문서 신고 성공",
		useReturnTypeSchema = true
	)
	@PostMapping("/documents/{documentId}")
	public ResponseTemplate<Void> reportDocument(
		@RequestBody ReportRequest reportRequest,
		@Parameter(description = "신고할 문서의 ID", example = "1")
		@PathVariable(name = "documentId") Long documentId,
		@Auth MemberInfo memberInfo) {

		reportService.reportDocument(documentId, reportRequest, memberInfo.getId());
		return ResponseTemplate.ok();
	}

	@Operation(summary = "토론 댓글 신고", description = "토론 댓글을 신고합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "토론 댓글 신고 성공",
		useReturnTypeSchema = true
	)
	@PostMapping("/comments/{commentId}")
	public ResponseTemplate<Void> reportComment(
		@RequestBody ReportRequest reportRequest,
		@Parameter(description = "신고할 토론 댓글의 ID", example = "1")
		@PathVariable(name = "commentId") Long commentId,
		@Auth MemberInfo memberInfo) {

		reportService.reportComment(commentId, reportRequest, memberInfo.getId());
		return ResponseTemplate.ok();
	}
}
