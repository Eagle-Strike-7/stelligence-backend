package goorm.eagle7.stelligence.domain.contribute.event;

/**
 * Contribute에 관련된 토론이 진행될 때 발생하는 이벤트
 * @param contributeId Contribute id
 */
public record ContributeDebatedEvent(Long contributeId) {

}
