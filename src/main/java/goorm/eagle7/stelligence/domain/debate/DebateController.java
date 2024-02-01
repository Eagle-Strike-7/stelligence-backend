package goorm.eagle7.stelligence.domain.debate;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.common.auth.memberinfo.Auth;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
import goorm.eagle7.stelligence.domain.debate.dto.CommentRequest;
import goorm.eagle7.stelligence.domain.debate.dto.CommentOrderCondition;
import goorm.eagle7.stelligence.domain.debate.dto.CommentPageResponse;
import goorm.eagle7.stelligence.domain.debate.dto.DebatePageResponse;
import goorm.eagle7.stelligence.domain.debate.dto.DebateResponse;
import goorm.eagle7.stelligence.domain.debate.model.DebateStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Debate API", description = "토론을 조회하고 종료하는 API를 제공합니다. 추가로 토론에 댓글을 작성/조회/삭제하는 API도 함께 제공합니다.")
@RequestMapping("/api/debates")
@RestController
@Slf4j
@RequiredArgsConstructor
public class DebateController {

	private final DebateService debateService;

	@Operation(summary = "토론 리스트 조회", description = "토론의 상태(OPEN / CLOSED)에 따라 토론 리스트를 조회합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "토론 리스트 조회 성공",
		useReturnTypeSchema = true
	)
	@GetMapping
	public ResponseTemplate<DebatePageResponse> getDebateList(
		@Parameter(description = "조회할 토론의 상태", example = "OPEN")
		@RequestParam("status") DebateStatus status,
		@ParameterObject
		@PageableDefault(page = 0, size = 10) Pageable pageable
	) {
		return ResponseTemplate.ok(debateService.getDebatePage(status, pageable));
	}

	@Operation(summary = "토론 상세 조회", description = "특정 토론을 조회합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "토론 상세 조회 성공",
		useReturnTypeSchema = true
	)
	@GetMapping("/{debateId}")
	public ResponseTemplate<DebateResponse> getDebateDetail(
		@Parameter(description = "조회할 토론의 ID", example = "1")
		@PathVariable("debateId") Long debateId
	) {
		return ResponseTemplate.ok(debateService.getDebateDetailById(debateId));
	}

	@Operation(summary = "토론 댓글 작성", description = "토론에 새로운 댓글을 작성합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "토론 댓글 작성 성공",
		useReturnTypeSchema = true
	)
	@PostMapping("/{debateId}/comments")
	public ResponseTemplate<Void> addComment(
		@Parameter(description = "댓글을 추가할 토론의 ID", example = "1")
		@PathVariable("debateId") Long debateId,
		@RequestBody CommentRequest commentRequest,
		@Auth MemberInfo memberInfo
	) {
		debateService.addComment(commentRequest, debateId, memberInfo.getId());
		return ResponseTemplate.ok();
	}

	@Operation(summary = "토론 댓글 삭제", description = "특정 토론의 특정 댓글을 삭제합니다. 해당 댓글을 작성했던 회원의 삭제 요청만이 허용됩니다.")
	@ApiResponse(
		responseCode = "200",
		description = "토론 댓글 삭제 성공",
		useReturnTypeSchema = true
	)
	@DeleteMapping("/{debateId}/comments/{commentId}")
	public ResponseTemplate<Void> deleteComment(
		@Parameter(description = "삭제할 댓글이 있는 토론 ID", example = "1")
		@PathVariable("debateId") Long debateId,
		@Parameter(description = "삭제할 댓글의 ID", example = "1")
		@PathVariable("commentId") Long commentId,
		@Auth MemberInfo memberInfo
	) {
		debateService.deleteComment(commentId, memberInfo.getId());
		return ResponseTemplate.ok();
	}

	@Operation(summary = "토론 댓글 조회", description = "특정 토론의 댓글을 조회합니다. 무한 스크롤을 위한 커서 페이징이 적용되어,"
		+ " 조회를 시작할 댓글 ID를 받아 해당 댓글로부터 size 개수 만큼 조회를 시작합니다."
		+ " order가 EARLIEST면 등록순, LATEST면 최신순으로 조회합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "토론 댓글 조회 성공",
		useReturnTypeSchema = true
	)
	@GetMapping("/{debateId}/comments")
	public ResponseTemplate<CommentPageResponse> getComments(
		@Parameter(description = "삭제할 댓글이 있는 토론 ID", example = "1")
		@PathVariable("debateId") Long debateId,
		@Parameter(description = "이전에 조회한 마지막 댓글의 ID, null이면 처음부터 조회", example = "1")
		@RequestParam(value = "cursor", required = false) Long cursor,
		@Parameter(description = "한번에 가져올 댓글의 개수", example = "10")
		@RequestParam(value = "size", defaultValue = "10") int size,
		@Parameter(description = "정렬 기준, 등록순이면 EARLIEST, 최신순이면 LATEST", example = "EARLIEST")
		@RequestParam(value = "order", defaultValue = "EARLIEST") CommentOrderCondition order
	) {
		return ResponseTemplate.ok();
	}

	@Operation(summary = "토론 댓글 수정", description = "특정 토론의 댓글을 수정합니다. 해당 댓글을 작성했던 회원의 수정 요청만이 허용됩니다.")
	@ApiResponse(
		responseCode = "200",
		description = "토론 댓글 조회 성공",
		useReturnTypeSchema = true
	)
	@PatchMapping("/{debateId}/comments/{commentId}")
	public ResponseTemplate<Void> updateComment(
		@Parameter(description = "수정할 댓글이 있는 토론의 ID", example = "1")
		@PathVariable("debateId") Long debateId,
		@Parameter(description = "수정할 댓글의 ID", example = "1")
		@PathVariable("commentId") Long commentId,
		@RequestBody CommentRequest commentRequest,
		@Auth MemberInfo memberInfo
	) {
		debateService.updateComment(commentId, commentRequest, memberInfo.getId());
		return ResponseTemplate.ok();
	}
}
