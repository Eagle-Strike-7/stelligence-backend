package goorm.eagle7.stelligence.domain.comment;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.common.auth.memberinfo.Auth;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
import goorm.eagle7.stelligence.domain.comment.dto.CommentCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Comment API", description = "토론 댓글을 작성하고 삭제하는 API를 제공합니다")
@RequestMapping("/api/comments")
@RestController
@Slf4j
@RequiredArgsConstructor
public class CommentController {

	@Operation(summary = "토론 댓글 작성", description = "토론에 새로운 댓글을 작성합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "토론 댓글 작성 성공",
		useReturnTypeSchema = true
	)
	@PostMapping
	public ResponseTemplate<Void> addComment(
		@RequestBody CommentCreateRequest commentCreateRequest,
		@Auth MemberInfo memberInfo
	) {
		return ResponseTemplate.ok();
	}

	@Operation(summary = "토론 댓글 삭제", description = "특정 토론 댓글을 삭제합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "토론 댓글 삭제 성공",
		useReturnTypeSchema = true
	)
	@DeleteMapping("/{commentId}")
	public ResponseTemplate<Void> deleteComment(
		@Parameter(description = "삭제할 토론 댓글의 ID", example = "1")
		@PathVariable("commentId") Long commentId,
		@Auth MemberInfo memberInfo
	) {
		return ResponseTemplate.ok();
	}
}
