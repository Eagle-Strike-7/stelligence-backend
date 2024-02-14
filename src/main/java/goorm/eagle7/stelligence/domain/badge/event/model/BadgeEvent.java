package goorm.eagle7.stelligence.domain.badge.event.model;

import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;

public record BadgeEvent (Long memberId, BadgeCategory badgeCategory) {
}
