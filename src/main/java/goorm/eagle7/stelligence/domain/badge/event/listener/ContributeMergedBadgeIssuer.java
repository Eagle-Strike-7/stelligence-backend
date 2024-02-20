package goorm.eagle7.stelligence.domain.badge.event.listener;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import goorm.eagle7.stelligence.domain.badge.BadgeService;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.event.ContributeMergedEvent;
import lombok.RequiredArgsConstructor;

/**
 * 수정요청이 반영되었을 때의 이벤트를 처리합니다.
 */
@Component
@RequiredArgsConstructor
public class ContributeMergedBadgeIssuer {

	private final ContributeRepository contributeRepository;
	private final BadgeService badgeService;

	/**
	 * <p>TransactionPhase.AFTER_COMMIT: 트랜잭션이 성공적으로 완료된 후에 이벤트를 처리합니다. (기본값)
	 *
	 * @param event 수정요청 완료 이벤트
	 */
	@Async
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@TransactionalEventListener(value = ContributeMergedEvent.class)
	public void onContributeMerged(ContributeMergedEvent event) {

		//배지를 확인하고 수여한다.
		contributeRepository
			.findWithMember(event.contributeId())
			.ifPresent(contribute ->
				badgeService.checkAndAwardBadge(
					BadgeCategory.CONTRIBUTE_MERGED
					, contribute.getMember())
			);

	}
	
}
