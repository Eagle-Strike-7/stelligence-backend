package goorm.eagle7.stelligence.domain.badge.event.model;

import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import lombok.Getter;

@Getter
public record BadgeEvent (Long memberId, BadgeCategory badgeCategory) {
}
