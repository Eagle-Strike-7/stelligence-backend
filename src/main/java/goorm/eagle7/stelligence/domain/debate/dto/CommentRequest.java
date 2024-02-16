package goorm.eagle7.stelligence.domain.debate.dto;

import goorm.eagle7.stelligence.domain.debate.model.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "작성/수정할 토론 댓글의 정보")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "of")
public class CommentRequest {

	@NotBlank(message = "댓글 내용을 입력해주세요.")
	@Size(max = Comment.MAX_COMMENT_LENGTH, message = "댓글 내용은 1000자 이하여야 합니다.")
	@Schema(description = "댓글 내용", example = "저도 동의합니다.")
	private String content;
}
