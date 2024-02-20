package goorm.eagle7.stelligence.domain.vote;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.vote.dto.VoteRequest;
import goorm.eagle7.stelligence.domain.vote.dto.VoteSummaryResponse;
import goorm.eagle7.stelligence.domain.vote.model.Vote;
import goorm.eagle7.stelligence.domain.vote.model.VoteSummary;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

	@Mock
	MemberRepository memberRepository;
	@Mock
	VoteRepository voteRepository;
	@Mock
	ContributeRepository contributeRepository;

	@InjectMocks
	VoteService voteService;

	@Test
	@DisplayName("투표하기 - 최초 투표")
	void noneToAgree() {
		Long requesterId = 1L;
		Long contributeId = 2L;

		Member member = member(requesterId, "pete");
		Contribute contribute = contribute(contributeId, member(2L, "paul"), "contributeTitle", "contributeDescription",
			ContributeStatus.VOTING, document(1L, member(3L, "nari"), "documentTitle", 3L));
		VoteRequest voteRequest = new VoteRequest(contributeId, true);

		//when
		when(memberRepository.findById(requesterId)).thenReturn(Optional.of(member));
		when(contributeRepository.findById(contributeId)).thenReturn(Optional.of(contribute));
		when(voteRepository.findByMemberAndContribute(member, contribute)).thenReturn(Optional.empty());
		when(voteRepository.getVoteSummary(contributeId)).thenReturn(new VoteSummary(1, 1));

		VoteSummaryResponse response = voteService.vote(voteRequest, requesterId);

		//then
		verify(voteRepository).save(any());
		assertThat(response.getAgreeCount()).isEqualTo(1);
		assertThat(response.getDisagreeCount()).isEqualTo(1);
		assertThat(response.getMyVote()).isEqualTo(true);
	}

	@Test
	@DisplayName("투표하기 - 반대로 변경")
	void agreeToDisagree() {
		Long requesterId = 1L;
		Long contributeId = 2L;

		Member member = member(requesterId, "pete");
		Contribute contribute = contribute(contributeId, member(2L, "paul"), "contributeTitle", "contributeDescription",
			ContributeStatus.VOTING, document(1L, member(3L, "nari"), "documentTitle", 3L));
		VoteRequest voteRequest = new VoteRequest(contributeId, false);
		Vote vote = Vote.createVote(member, contribute, true);

		//when
		when(memberRepository.findById(requesterId)).thenReturn(Optional.of(member));
		when(contributeRepository.findById(contributeId)).thenReturn(Optional.of(contribute));
		when(voteRepository.findByMemberAndContribute(member, contribute)).thenReturn(Optional.of(vote));
		when(voteRepository.getVoteSummary(contributeId)).thenReturn(new VoteSummary(1, 1));

		VoteSummaryResponse response = voteService.vote(voteRequest, requesterId);

		//then
		verify(voteRepository, never()).save(any());
		assertThat(response.getAgreeCount()).isEqualTo(1);
		assertThat(response.getDisagreeCount()).isEqualTo(1);
		assertThat(response.getMyVote()).isFalse();
	}

	@Test
	@DisplayName("투표 현황 조회")
	void getVoteSummary() {
		Long contributeId = 1L;
		Long memberId = 5L;

		Member member = member(memberId, "pete");
		Contribute contribute = contribute(contributeId, member(2L, "paul"), "contributeTitle", "contributeDescription",
			ContributeStatus.VOTING, document(1L, member(3L, "nari"), "documentTitle", 3L));

		//when
		when(contributeRepository.findById(contributeId)).thenReturn(Optional.of(contribute));
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
		when(voteRepository.findByMemberAndContribute(member, contribute)).thenReturn(Optional.empty());
		when(voteRepository.getVoteSummary(contributeId)).thenReturn(new VoteSummary(1, 1));

		VoteSummaryResponse response = voteService.getVoteSummary(contributeId, memberId);

		//then
		assertThat(response.getAgreeCount()).isEqualTo(1);
		assertThat(response.getDisagreeCount()).isEqualTo(1);
		assertThat(response.getMyVote()).isNull();
	}

}