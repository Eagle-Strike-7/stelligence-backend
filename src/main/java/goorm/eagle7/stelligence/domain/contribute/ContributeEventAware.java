package goorm.eagle7.stelligence.domain.contribute;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.common.event.contribute.ContributeMergedEvent;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.notification.NotificationRequest;
import goorm.eagle7.stelligence.domain.notification.NotificationSender;
import goorm.eagle7.stelligence.domain.vote.VoteRepository;
import lombok.RequiredArgsConstructor;

/**
 * 수정요청과 관련된 이벤트를 처리합니다.
 *
 * <p>Async: 이벤트를 전달받아 이벤트 발생 쓰레드와는 별도의 쓰레드에서 처리합니다.
 */
@Async
@Transactional
@Component
@RequiredArgsConstructor
public class ContributeEventAware {

	private final ContributeRepository contributeRepository;
	private final VoteRepository voteRepository;
	private final NotificationSender notificationSender;

	private static final String CONTRIBUTE_MERGED_MESSAGE = "수정요청 '%s'이(가) 반영되었습니다! 글을 확인해보세요.";
	private static final String DOCUMENT_URI = "/stars/%d";

	/**
	 * 수정요청이 완료되면 수정요청 게시자와 투표자에게 알림을 보낸다.
	 * @param event 수정요청 완료 이벤트
	 */
	@EventListener(ContributeMergedEvent.class)
	public void onContributeMerged(ContributeMergedEvent event) {
		Contribute contribute = contributeRepository.findWithMember(event.contributeId())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 수정요청입니다."));

		//알림을 받을 대상을 찾는다.
		Set<Long> targets = new HashSet<>();

		targets.addAll(voteRepository.findVoters(event.contributeId()));
		targets.add(contribute.getMember().getId());

		//알림 요청 객체를 생성한다.
		String slicedTitle = contribute.getTitle().length() > 20 ? contribute.getTitle().substring(0, 20) + "..." :
			contribute.getTitle();

		NotificationRequest request = NotificationRequest.of(
			String.format(CONTRIBUTE_MERGED_MESSAGE, slicedTitle),
			String.format(DOCUMENT_URI, contribute.getDocument().getId()),
			targets
		);

		//알림을 보낸다.
		notificationSender.send(request);
	}
}
