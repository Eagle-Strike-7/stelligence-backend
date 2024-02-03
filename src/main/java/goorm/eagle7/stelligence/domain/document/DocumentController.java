package goorm.eagle7.stelligence.domain.document;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.common.auth.memberinfo.Auth;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
import goorm.eagle7.stelligence.domain.document.content.dto.DocumentResponse;
import goorm.eagle7.stelligence.domain.document.dto.DocumentCreateRequest;
import goorm.eagle7.stelligence.domain.document.graph.dto.DocumentGraphResponse;
import goorm.eagle7.stelligence.domain.document.graph.dto.DocumentNodeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 문서 관련 API를 제공하는 컨트롤러입니다.
 * * 문서 생성
 * * 문서 버전 별 조회
 */
@Tag(name = "Document API", description = "문서를 생성하고 조회하는 API를 제공합니다")
@Slf4j
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

	private final DocumentService documentService;

	@Operation(summary = "문서 생성", description = "문서를 생성합니다")
	@ApiResponse(
		responseCode = "200",
		description = "문서 생성 성공",
		useReturnTypeSchema = true
	)
	@PostMapping
	public ResponseTemplate<DocumentResponse> createDocument(
		@RequestBody @Validated DocumentCreateRequest documentCreateRequest,
		@Auth MemberInfo memberInfo
	) {
		return ResponseTemplate.ok(documentService.createDocument(documentCreateRequest, memberInfo.getId()));
	}

	@Operation(summary = "문서 내용 조회", description = "문서의 내용을 조회합니다")
	@ApiResponse(
		responseCode = "200",
		description = "문서 조회 성공",
		useReturnTypeSchema = true
	)
	@GetMapping("/{documentId}")
	public ResponseTemplate<DocumentResponse> getDocument(
		@Parameter(description = "조회할 문서의 ID", example = "1")
		@PathVariable Long documentId,
		@Parameter(description = "문서의 특정 버전을 가져올 수 있습니다. 전달되지 않는 경우 기본값으로 최신본을 반환합니다", example = "1")
		@RequestParam(required = false) Long revision
	) {
		//revision이 null인 경우는 service에서 최신값을 찾아 반환하도록 되어있습니다.
		return ResponseTemplate.ok(documentService.getDocumentContent(documentId, revision));
	}

	@Operation(summary = "문서 그래프 조회", description = "문서 그래프를 조회합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "문서 그래프 조회 성공",
		useReturnTypeSchema = true
	)
	@GetMapping
	public ResponseTemplate<DocumentGraphResponse> getDocumentGraph(
		@Parameter(description = "조회를 시작할 문서의 ID. 입력하지 않으면 최상위 문서를 조회합니다.", example = "1")
		@RequestParam(value = "documentId", required = false) Long documentId,
		@Parameter(description = "조회할 문서의 깊이. 입력하지 않으면 깊이가 0으로 설정됩니다.", example = "1")
		@RequestParam(value = "depth", defaultValue = "0") int depth
	) {

		// documentId가 null이라면 최상위 문서로부터 조회합니다.
		return ResponseTemplate.ok(documentService.getDocumentGraph(documentId, depth));
	}

	@Operation(summary = "문서 노드 제목으로 조회", description = "문서 노드를 제목으로 조회합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "문서 노드 제목 조회 성공",
		useReturnTypeSchema = true
	)
	@GetMapping("/search")
	public ResponseTemplate<List<DocumentNodeResponse>> searchDocument(
		@Parameter(description = "검색할 제목", example = "제목")
		@RequestParam("title") String title,
		@Parameter(description = "최대 검색 결과의 개수. 입력하지 않으면 최대 10개를 조회합니다.", example = "10")
		@RequestParam(value = "limit", defaultValue = "10") int limit
	) {

		return ResponseTemplate.ok(documentService.getDocumentNodeByTitle(title, limit));
	}
}
