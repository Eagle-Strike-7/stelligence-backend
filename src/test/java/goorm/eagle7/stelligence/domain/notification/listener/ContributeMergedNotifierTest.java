package goorm.eagle7.stelligence.domain.notification.listener;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.common.util.Site;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.event.ContributeMergedEvent;
import goorm.eagle7.stelligence.domain.contribute.event.listener.ContributeMergedEventListener;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.notification.NotificationRequest;
import goorm.eagle7.stelligence.domain.notification.NotificationSender;
import goorm.eagle7.stelligence.domain.vote.VoteRepository;

@ExtendWith(MockitoExtension.class)
class ContributeMergedNotifierTest {

	@Mock
	NotificationSender notificationSender;
	@Mock
	VoteRepository voteRepository;
	@Mock
	ContributeRepository contributeRepository;
	@InjectMocks
	ContributeMergedEventListener contributeEventAware;

	@Test
	@DisplayName("수정요청 완료 이벤트를 받아 알림을 보낸다.")
	void mergedEventTest() {

		// given
		ContributeMergedEvent event = new ContributeMergedEvent(1L);
		Member member = member(1L, "pete");
		Document document = document(1L, member, "documentTitle", 2L);
		Contribute contribute = contribute(1L, member, "contributeTitle", "description", ContributeStatus.MERGED,
			document);

		// when
		when(voteRepository.findVoters(event.contributeId())).thenReturn(new HashSet<>(Set.of(1L, 2L, 3L)));
		when(contributeRepository.findWithMember(event.contributeId())).thenReturn(Optional.of(contribute));
		contributeEventAware.onContributeMerged(event);

		// then
		verify(notificationSender).send(
			NotificationRequest.of(
				"수정요청 'contributeTitle'이(가) 반영되었습니다! 글을 확인해보세요.",
				"/stars/1",
				new HashSet<>(Set.of(1L, 2L, 3L))
			)
		);
	}

	@Test
	@DisplayName("제목이 긴 경우 정상적으로 문자열이 잘리는지 확인한다.")
	void mergedEventWithLongTitleTest() {

		// given
		ContributeMergedEvent event = new ContributeMergedEvent(1L);
		Member member = member(1L, "pete");
		Document document = document(1L, member, "title", 2L);
		Contribute contribute = contribute(1L, member, "title6789012345678901234567890", "description",
			ContributeStatus.MERGED,
			document);

		// when
		when(voteRepository.findVoters(event.contributeId())).thenReturn(new HashSet<>(Set.of(1L, 2L, 3L)));
		when(contributeRepository.findWithMember(event.contributeId())).thenReturn(Optional.of(contribute));
		contributeEventAware.onContributeMerged(event);

		// then
		verify(notificationSender).send(
			NotificationRequest.of(
				"수정요청 'title678901234567890...'이(가) 반영되었습니다! 글을 확인해보세요.",
				Site.document(1L),
				new HashSet<>(Set.of(1L, 2L, 3L))
			)
		);
	}

	@Test
	@DisplayName("수정요청 게시자 역시 알림을 받는다.")
	void notifyContributor() {

		// given
		ContributeMergedEvent event = new ContributeMergedEvent(1L);
		Member member = member(4L, "pete");
		Document document = document(1L, member, "title", 2L);
		Contribute contribute = contribute(1L, member, "title", "description",
			ContributeStatus.MERGED,
			document);

		// when
		when(voteRepository.findVoters(event.contributeId())).thenReturn(new HashSet<>(Set.of(1L, 2L, 3L)));
		when(contributeRepository.findWithMember(event.contributeId())).thenReturn(Optional.of(contribute));
		contributeEventAware.onContributeMerged(event);

		// then
		verify(notificationSender).send(
			NotificationRequest.of(
				"수정요청 'title'이(가) 반영되었습니다! 글을 확인해보세요.",
				Site.document(1L),
				new HashSet<>(Set.of(1L, 2L, 3L, 4L))
			)
		);
	}

}