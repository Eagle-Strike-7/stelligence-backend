package goorm.eagle7.stelligence.domain.notification.listener;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.event.ContributeRejectedEvent;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.notification.NotificationRequest;
import goorm.eagle7.stelligence.domain.notification.NotificationSender;
import goorm.eagle7.stelligence.domain.notification.listener.contribute.ContributeRejectedNotifier;
import goorm.eagle7.stelligence.domain.vote.VoteRepository;

@ExtendWith(MockitoExtension.class)
class ContributeRejectedNotifierTest {

	@Mock
	NotificationSender notificationSender;
	@Mock
	VoteRepository voteRepository;
	@Mock
	ContributeRepository contributeRepository;
	@InjectMocks
	ContributeRejectedNotifier contributeRejectedNotifier;

	@Test
	void onContributeRejected() {
		// given
		ContributeRejectedEvent event = new ContributeRejectedEvent(1L);
		Member member = member(1L, "pete");
		Document document = document(1L, member, "documentTitle", 2L);
		Contribute contribute = contribute(1L, member, "contributeTitle", "description", ContributeStatus.MERGED,
			document);

		// when
		when(voteRepository.findVoters(event.contributeId())).thenReturn(new HashSet<>(Set.of(1L, 2L, 3L)));
		when(contributeRepository.findWithMember(event.contributeId())).thenReturn(Optional.of(contribute));
		contributeRejectedNotifier.onContributeRejected(event);

		// then
		verify(notificationSender).send(
			NotificationRequest.of(
				"수정요청 'contributeTitle'이(가) 반려되었습니다. 투표 결과를 확인해보세요",
				"/revise/1/vote",
				new HashSet<>(Set.of(1L, 2L, 3L))
			)
		);

	}

}