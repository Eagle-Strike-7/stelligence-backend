package goorm.eagle7.stelligence.domain.notification.listener.contribute;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.event.ContributeMergedEvent;
import goorm.eagle7.stelligence.domain.contribute.event.ContributeRejectedEvent;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.notification.NotificationRequest;
import goorm.eagle7.stelligence.domain.notification.NotificationSender;
import goorm.eagle7.stelligence.domain.notification.util.StringSlicer;
import goorm.eagle7.stelligence.domain.vote.VoteRepository;
import lombok.RequiredArgsConstructor;

/**
 * 수정요청 반려 이벤트가 발생했을 때, 이 정보를 사용자에게 알리는 역할을 하는 클래스입니다.
 */
@Component
@RequiredArgsConstructor
public class ContributeRejectedNotifier {

	private final ContributeRepository contributeRepository;
	private final VoteRepository voteRepository;
	private final NotificationSender notificationSender;

	private static final String CONTRIBUTE_REJECTED_MESSAGE = "수정요청 '%s'이(가) 반려되었습니다. 투표 결과를 확인해보세요";
	private static final String VOTE_URI = "/revise/%d/vote";

	/**
	 * 수정요청이 반려되었을 때의 이벤트를 처리합니다.
	 * @param event 수정요청 반려 이벤트
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@TransactionalEventListener(value = ContributeRejectedEvent.class)
	public void onContributeMerged(ContributeMergedEvent event) {
		Contribute contribute = contributeRepository.findWithMember(event.contributeId())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 수정요청입니다."));

		//알림을 받을 대상을 찾는다.
		Set<Long> targets = new HashSet<>();

		targets.addAll(voteRepository.findVoters(event.contributeId()));
		targets.add(contribute.getMember().getId());

		NotificationRequest request = NotificationRequest.of(
			String.format(CONTRIBUTE_REJECTED_MESSAGE, StringSlicer.slice(contribute.getTitle())),
			String.format(VOTE_URI, contribute.getDocument().getId()),
			targets
		);

		//알림을 보낸다.
		notificationSender.send(request);
	}
}
