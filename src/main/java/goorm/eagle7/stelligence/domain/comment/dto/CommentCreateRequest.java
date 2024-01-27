package goorm.eagle7.stelligence.domain.comment.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentCreateRequest {

	private Long debateId;
	private String content;
}
