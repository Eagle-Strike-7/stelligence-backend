package goorm.eagle7.stelligence.domain.vote;

import org.springframework.stereotype.Service;

import goorm.eagle7.stelligence.domain.vote.dto.VoteRequest;

@Service
public class VoteService {

	//투표
	public void vote(VoteRequest voteRequest, Long loginMemberId) {
	}

	public void cancelVote(Long contributeId, Long loginMemberId) {
	}
}
