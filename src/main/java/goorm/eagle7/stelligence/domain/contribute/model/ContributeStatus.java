package goorm.eagle7.stelligence.domain.contribute.model;

/**
 * ContributeStatus
 * Contribute의 상태를 나타내는 enum 클래스입니다.
 */
public enum ContributeStatus {
	/**
	 * 수정요청이 생성된 뒤 사용자에게 투표를 뱓고 있는 상태이며, 최대 24시간까지 유지됩니다.
	 */
	VOTING,

	/**
	 * 투표 기간이 종료된 후 충분한 투표율을 받지 못하여 토론으로 넘어간 상태입니다.
	 */
	DEBATING,

	/**
	 * 투표 기간이 종료된 후 낮은 투표율로 인하여 수정요청이 거절된 상태입니다.
	 */
	REJECTED,

	/**
	 * 투표 기간이 종료된 후 충분한 투표율로 인하여 수정요청이 반영된 상태입니다.
	 */
	MERGED
}
