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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/api/comments")
@RestController
@Slf4j
@RequiredArgsConstructor
public class CommentController {

	@PostMapping
	public ResponseTemplate<Void> addComment(
		@RequestBody CommentCreateRequest commentCreateRequest,
		@Auth MemberInfo memberInfo
	) {
		return ResponseTemplate.ok();
	}

	@DeleteMapping("/{commentId}")
	public ResponseTemplate<Void> deleteComment(
		@PathVariable("commentId") Long commentId,
		@Auth MemberInfo memberInfo
	) {
		return ResponseTemplate.ok();
	}
}
