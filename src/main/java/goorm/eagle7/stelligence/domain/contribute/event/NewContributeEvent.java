package goorm.eagle7.stelligence.domain.contribute.event;

/**
 * 새로운 Contribute가 생성될 때 발생하는 이벤트
 * @param contributeId 수정요청 ID
 */
public record NewContributeEvent(Long contributeId) {

}
