package goorm.eagle7.stelligence.domain.debate.dto;

import java.time.LocalDateTime;

import goorm.eagle7.stelligence.domain.debate.model.Comment;
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
	private int sequence;

	// 댓글 작성자 정보
	private MemberSimpleResponse commenter;

	public static CommentResponse from(Comment comment) {
		return new CommentResponse(comment);
	}

	private CommentResponse(Comment comment) {
		this.commentId = comment.getId();
		this.content = comment.getContent();
		this.createdAt = comment.getCreatedAt();
		this.sequence = comment.getSequence();
		this.commenter = MemberSimpleResponse.from(comment.getCommenter());

	}
}
