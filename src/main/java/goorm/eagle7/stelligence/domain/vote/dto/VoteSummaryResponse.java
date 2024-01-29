package goorm.eagle7.stelligence.domain.vote.dto;

import goorm.eagle7.stelligence.domain.vote.model.VoteAction;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VoteSummaryResponse {

	private Long agreeCount;    //찬성 개수
	private Long disagreeCount;    //반대 개수
	private VoteAction myVoteAction;    //내 투표 상태
}
