package goorm.eagle7.stelligence.domain.contribute.scheduler;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.vote.VoteCustomRepository;
import goorm.eagle7.stelligence.domain.vote.VoteSummary;

@ExtendWith(MockitoExtension.class)
class ContributeSchedulingActionDeterminerTest {

	@Mock
	VoteCustomRepository voteCustomRepository;

	@InjectMocks
	ContributeSchedulingActionDeterminer contributeSchedulingActionDeterminer;

	@Test
	void merge() {
		//given
		Contribute contribute = contribute(1L, null, ContributeStatus.VOTING, null);

		//when
		when(voteCustomRepository.getVoteSummary(contribute.getId())).thenReturn(new VoteSummary(100, 80));
		ContributeSchedulingAction action = contributeSchedulingActionDeterminer.check(contribute);

		//then
		assertThat(action).isEqualTo(ContributeSchedulingAction.MERGE);
	}

	@Test
	void debate() {
		//given
		Contribute contribute = contribute(1L, null, ContributeStatus.VOTING, null);

		//when
		when(voteCustomRepository.getVoteSummary(contribute.getId())).thenReturn(new VoteSummary(100, 79));
		ContributeSchedulingAction action = contributeSchedulingActionDeterminer.check(contribute);

		//then
		assertThat(action).isEqualTo(ContributeSchedulingAction.DEBATE);
	}

	@Test
	void reject() {
		//given
		Contribute contribute = contribute(1L, null, ContributeStatus.VOTING, null);

		//when
		when(voteCustomRepository.getVoteSummary(contribute.getId())).thenReturn(new VoteSummary(100, 29));
		ContributeSchedulingAction action = contributeSchedulingActionDeterminer.check(contribute);

		//then
		assertThat(action).isEqualTo(ContributeSchedulingAction.REJECT);
	}

}