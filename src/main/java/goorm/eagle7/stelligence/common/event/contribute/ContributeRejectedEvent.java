package goorm.eagle7.stelligence.common.event.contribute;

/**
 * Contribute가 반려될 때 발생하는 이벤트
 * @param contributeId 수정요청 ID
 */
public record ContributeRejectedEvent(Long contributeId) {

}
