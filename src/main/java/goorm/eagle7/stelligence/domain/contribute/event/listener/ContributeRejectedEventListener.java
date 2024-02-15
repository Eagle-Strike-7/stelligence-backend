package goorm.eagle7.stelligence.domain.contribute.event.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import goorm.eagle7.stelligence.domain.badge.BadgeService;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.event.ContributeRejectedEvent;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ContributeRejectedEventListener {

	private final BadgeService badgeService;
	private final ContributeRepository contributeRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@TransactionalEventListener(value = ContributeRejectedEvent.class)
	public void onContributeRejected(ContributeRejectedEvent event) {

		if (contributeRepository.existsById(event.contributeId())) {
			contributeRepository.findWithMember(event.contributeId())
				.ifPresent(contribute ->
					badgeService.checkAndAwardBadge(BadgeCategory.CONTRIBUTE_REJECTED, contribute.getMember())
				);
		}
	}

}
