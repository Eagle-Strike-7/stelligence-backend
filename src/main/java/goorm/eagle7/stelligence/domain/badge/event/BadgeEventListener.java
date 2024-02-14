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

	/**
	 * <h2>BadgeEvent를 받아서 BadgeService를 통해 Badge를 수여</h2>
	 * <p>- 새로운 스레드에서 비동기로 진행</p>
	 * <p>- REQUIRES_NEW로 트랜잭션 분리</p>
	 * <p>- BadgeEvent에만 반응</p>
	 * <p>- event에서 member가 아닌 memberId를 꺼내 와야 변경 정상 작동</p>
	 * @param event BadgeEvent
	 */
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
