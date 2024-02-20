package goorm.eagle7.stelligence.domain.contribute.scheduler;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.vote.custom.CustomVoteRepository;
import goorm.eagle7.stelligence.domain.vote.model.VoteSummary;

@ExtendWith(MockitoExtension.class)
class ContributeSchedulingActionDeterminerTest {

	@Mock
	CustomVoteRepository voteCustomRepository;

	@InjectMocks
	ContributeSchedulingActionDeterminer contributeSchedulingActionDeterminer;

	@Test
	void merge() {
		//given
		Document document = document(1L, null, "title", null);
		Contribute contribute = contribute(1L, null, "title", "description", ContributeStatus.VOTING, document);

		//when
		when(voteCustomRepository.getVoteSummary(contribute.getId())).thenReturn(
			new VoteSummary(80, 20));
		ContributeSchedulingAction action = contributeSchedulingActionDeterminer.check(contribute);

		//then
		assertThat(action).isEqualTo(ContributeSchedulingAction.MERGE);
	}

	@Test
	void debate() {
		//given
		Document document = document(1L, null, "title", null);
		Contribute contribute = contribute(1L, null, "title", "description", ContributeStatus.VOTING, document);

		//when
		when(voteCustomRepository.getVoteSummary(contribute.getId())).thenReturn(
			new VoteSummary(40, 20));
		ContributeSchedulingAction action = contributeSchedulingActionDeterminer.check(contribute);

		//then
		assertThat(action).isEqualTo(ContributeSchedulingAction.DEBATE);
	}

	@Test
	void reject() {
		//given
		Document document = document(1L, null, "title", null);
		Contribute contribute = contribute(1L, null, "title", "description", ContributeStatus.VOTING, document);

		//when
		when(voteCustomRepository.getVoteSummary(contribute.getId())).thenReturn(
			new VoteSummary(19, 80));
		ContributeSchedulingAction action = contributeSchedulingActionDeterminer.check(contribute);

		//then
		assertThat(action).isEqualTo(ContributeSchedulingAction.REJECT);
	}

	@Test
	@DisplayName("투표가 없으면 반려")
	void rejectWhenNoVote() {
		//given
		Document document = document(1L, null, "title", null);
		Contribute contribute = contribute(1L, null, "title", "description", ContributeStatus.VOTING, document);

		//when
		when(voteCustomRepository.getVoteSummary(contribute.getId())).thenReturn(
			new VoteSummary(0, 0));
		ContributeSchedulingAction action = contributeSchedulingActionDeterminer.check(contribute);

		//then
		assertThat(action).isEqualTo(ContributeSchedulingAction.REJECT);
	}

}