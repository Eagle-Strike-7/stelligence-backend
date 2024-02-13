package goorm.eagle7.stelligence.domain.notification.listener;

import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import goorm.eagle7.stelligence.domain.bookmark.BookmarkRepository;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.event.ContributeMergedEvent;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.notification.NotificationRequest;
import goorm.eagle7.stelligence.domain.notification.NotificationSender;
import goorm.eagle7.stelligence.domain.notification.util.StringSlicer;
import lombok.RequiredArgsConstructor;

/**
 * 북마크한 문서가 수정되었을 때 알림을 보내는 리스너
 *
 * <p>알림의 대상 : 해당 문서를 북마크한 사용자
 */
@Component
@RequiredArgsConstructor
public class BookmarkedDocumentChangedNotifier {

	private final ContributeRepository contributeRepository;
	private final BookmarkRepository bookmarkRepository;
	private final NotificationSender notificationSender;

	private static final String BOOKMARKED_DOCUMENT_CHANGED_MESSAGE = "북마크한 문서 '%s'(이)가 수정되었습니다. 확인해보세요!";
	private static final String DOCUMENT_URI = "/stars/%d";

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@TransactionalEventListener(value = ContributeMergedEvent.class)
	public void onContributeMerged(ContributeMergedEvent event) {
		Contribute contribute = contributeRepository.findById(event.contributeId())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 수정요청입니다."));

		// 북마크한 사용자들을 찾는다.
		// 이 시점에서 lazy loading으로 인해 document에 대한 쿼리가 발생합니다.
		Set<Long> bookmarkedMembers = bookmarkRepository.findBookmarkedMemberIdByDocumentId(
			contribute.getDocument().getId());

		// 알림 요청 객체를 생성한다.
		NotificationRequest request = NotificationRequest.of(
			String.format(BOOKMARKED_DOCUMENT_CHANGED_MESSAGE, StringSlicer.slice(contribute.getDocument().getTitle())),
			String.format(DOCUMENT_URI, contribute.getDocument().getId()),
			bookmarkedMembers
		);

		//알림을 보낸다.
		notificationSender.send(request);
	}

}
