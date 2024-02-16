package goorm.eagle7.stelligence.domain.badge.event;

import goorm.eagle7.stelligence.domain.badge.model.Badge;

/**
 * 새로운 배지 추가 시 발생하는 이밴트
 * @param memberId 회원 아이디
 * @param badge 추가된 배지
 */
public record NewBadgeEvent(Long memberId, Badge badge) {
}
