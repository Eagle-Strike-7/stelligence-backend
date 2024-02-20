package goorm.eagle7.stelligence.domain.notification.listener;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.common.util.Site;
import goorm.eagle7.stelligence.domain.bookmark.BookmarkRepository;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.event.ContributeMergedEvent;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.notification.NotificationSender;
import goorm.eagle7.stelligence.domain.notification.dto.request.NotificationRequest;

@ExtendWith(MockitoExtension.class)
class BookmarkedDocumentChangedNotifierTest {

	@Mock
	ContributeRepository contributeRepository;
	@Mock
	BookmarkRepository bookmarkRepository;
	@Mock
	NotificationSender notificationSender;
	@InjectMocks
	BookmarkedDocumentChangedNotifier bookmarkedDocumentChangedNotifier;

	@Test
	@DisplayName("북마크한 문서가 수정되었을 때 알림을 보내야한다.")
	void onContributeMerged() {
		// given
		Document document = document(1L, null, "documentTitle", 1L);
		Contribute contribute = contribute(1L, null, "contributeTitle", "desc", ContributeStatus.MERGED, document);

		// when
		when(contributeRepository.findById(1L))
			.thenReturn(Optional.of(contribute));
		when(bookmarkRepository.findBookmarkedMemberIdByDocumentId(1L))
			.thenReturn(Set.of(3L, 4L, 5L, 6L));

		bookmarkedDocumentChangedNotifier.onContributeMerged(new ContributeMergedEvent(1L));

		// then
		verify(notificationSender).send(
			NotificationRequest.of(
				"북마크한 문서 'documentTitle'(이)가 수정되었습니다. 확인해보세요!",
				Site.document(1L),
				Set.of(3L, 4L, 5L, 6L)
			)
		);
	}

}