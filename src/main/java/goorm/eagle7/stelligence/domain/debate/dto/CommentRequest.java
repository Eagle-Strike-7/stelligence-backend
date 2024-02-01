package goorm.eagle7.stelligence.domain.debate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "작성/수정할 토론 댓글의 정보")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentRequest {

	@Schema(description = "댓글 내용", example = "저도 동의합니다.")
	private String content;
}
