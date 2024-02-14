package goorm.eagle7.stelligence.domain.debate.event;

/**
 * 토론 종료 이벤트
 * @param debateId - 토론 ID
 */
public record DebateEndEvent(Long debateId) {
}
