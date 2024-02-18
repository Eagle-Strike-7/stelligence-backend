package goorm.eagle7.stelligence.domain.notification.listener.contribute;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import goorm.eagle7.stelligence.common.util.Site;
import goorm.eagle7.stelligence.domain.contribute.event.ContributeDebatedEvent;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.repository.DebateRepository;
import goorm.eagle7.stelligence.domain.notification.NotificationRequest;
import goorm.eagle7.stelligence.domain.notification.NotificationSender;
import goorm.eagle7.stelligence.domain.notification.util.StringSlicer;
import goorm.eagle7.stelligence.domain.vote.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 수정요청이 종료되고 토론이 열렸을때의 알림을 처리합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContributeDebatedNotifier {

	private final DebateRepository debateRepository;
	private final VoteRepository voteRepository;
	private final NotificationSender notificationSender;

	private static final String CONTRIBUTE_DEBATED_MESSAGE = "수정요청 '%s'와 관련된 토론이 시작되었습니다! 토론을 확인해보세요.";

	/**
	 * Contribute가 토론이 완료되었을 때 알림을 보냅니다.
	 * @param event 토론 시작 이벤트
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@TransactionalEventListener(value = ContributeDebatedEvent.class)
	public void onContributeDebated(ContributeDebatedEvent event) {
		Long debateId = event.debateId();

		Debate debate = debateRepository.findByIdWithContributeWithoutAmendment(debateId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 토론입니다."));

		//대상을 찾는다.
		Set<Long> targets = new HashSet<>();

		targets.addAll(voteRepository.findVoters(debate.getContribute().getId()));
		targets.add(debate.getContribute().getMember().getId());

		//알림 요청 객체를 생성한다.
		NotificationRequest request = NotificationRequest.of(
			String.format(CONTRIBUTE_DEBATED_MESSAGE, StringSlicer.slice(debate.getContribute().getTitle())),
			Site.debate(debateId),
			targets
		);

		//알림을 보낸다.
		notificationSender.send(request);
	}
}
