package goorm.eagle7.stelligence.domain.notification.model;

/**
 * 알림 타입
 */
public enum NotificationType {

	/**
	 * 수정요청이 원본에 반영되었음
	 제	 * 대상 : 수정 요청자, 투표자
	 */
	CONTRIBUTE_MERGED,

	/**
	 * 수정요청이 토론으로 넘어감
	 * 대상 : 수정 요청자, 투표자
	 */
	CONTRIBUTE_DEBATED,

	/**
	 * 수정요청이 거절됨
	 * 대상 : 수정 요청자, 투표자
	 */
	CONTRIBUTE_REJECT,

	/**
	 * 토론이 종료됨
	 * 대상 : 수정 요청자, 토론에 댓글 단 사람
	 */
	DEBATE_END,

	/**
	 * 새로운 뱃지 획득
	 * 대상 : 뱃지 획득자
	 */
	NEW_BADGE,

	/**
	 * 댓글이 추가됨
	 * 대상 : 수정 요청자, 토론에 댓글 단 사람
	 */
	COMMENT_ADDED,

	/**
	 * 북마크한 문서가 수정됨
	 * 대상 : 북마크한 사람
	 */
	BOOKMARKED_DOCUMENT_CHANGED
}
