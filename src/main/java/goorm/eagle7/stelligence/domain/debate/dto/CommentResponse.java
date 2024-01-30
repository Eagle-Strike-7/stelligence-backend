package goorm.eagle7.stelligence.domain.debate.dto;

import java.time.LocalDateTime;

import goorm.eagle7.stelligence.domain.member.dto.MemberSimpleResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentResponse {

	// 댓글 정보
	private Long commentId;
	private String content;
	private LocalDateTime createdAt;

	// 댓글 작성자 정보
	private MemberSimpleResponse commenter;

}
