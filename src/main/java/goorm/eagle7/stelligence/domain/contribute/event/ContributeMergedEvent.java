package goorm.eagle7.stelligence.domain.contribute.event;

/**
 * Contribute가 병합될 때 발생하는 이벤트
 * @param contributeId 수정요청 ID
 */
public record ContributeMergedEvent(Long contributeId) {

}
