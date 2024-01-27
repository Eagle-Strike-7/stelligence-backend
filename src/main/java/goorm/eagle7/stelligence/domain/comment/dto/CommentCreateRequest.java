package goorm.eagle7.stelligence.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "작성할 토론 댓글의 정보")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentCreateRequest {

	@Schema(description = "댓글을 추가할 토론의 ID", example = "1")
	private Long debateId;
	@Schema(description = "추가할 댓글 내용", example = "저도 동의합니다.")
	private String content;
}
