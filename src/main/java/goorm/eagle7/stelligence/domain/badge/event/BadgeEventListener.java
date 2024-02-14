package goorm.eagle7.stelligence.domain.badge.event;

import static org.springframework.transaction.annotation.Propagation.*;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import goorm.eagle7.stelligence.domain.badge.BadgeService;
import goorm.eagle7.stelligence.domain.badge.event.model.BadgeEvent;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BadgeEventListener {

	private final MemberRepository memberRepository;
	private final BadgeService badgeService;

	@Async
	@Transactional(propagation = REQUIRES_NEW)
	@TransactionalEventListener(BadgeEvent.class)
	public void handle(BadgeEvent event) {

		memberRepository.findById(event.memberId())
			.ifPresent(member ->
				// badgeCategory에 해당하는 전략을 찾아서 수행
				badgeService.checkAndAwardBadge(event.badgeCategory(), member)
			);

	}

}
