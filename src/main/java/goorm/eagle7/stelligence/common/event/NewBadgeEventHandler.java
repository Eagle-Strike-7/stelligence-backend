package goorm.eagle7.stelligence.common.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NewBadgeEventHandler {

	// TODO BadgeService 필요
	// private final BadgeService badgeService;

	// TODO BadgeService 필요
	// @TransactionalEventListener()
	// public void handle(NewBadgeFromSignUpEvent event) {
	// 	badgeService.createBadge(event.getMemberId(), event.getBadgeType());
	// }

}
