package goorm.eagle7.stelligence.domain.debate.event;

/**
 * 새로운 댓글이 생성될 때 발생하는 이벤트
 * @param commentId 댓글 ID
 */
public record NewCommentEvent(Long commentId) {
}
