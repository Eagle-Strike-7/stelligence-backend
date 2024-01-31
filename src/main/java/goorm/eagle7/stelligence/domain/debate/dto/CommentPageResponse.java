package goorm.eagle7.stelligence.domain.debate.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentPageResponse {

	// 댓글 정보
	private List<CommentResponse> comments;

	// 다음 페이지 존재 여부
	private boolean hasNext;

}
