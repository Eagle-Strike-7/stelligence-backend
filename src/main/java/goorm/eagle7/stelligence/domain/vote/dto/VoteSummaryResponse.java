package goorm.eagle7.stelligence.domain.vote.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VoteSummaryResponse {

	private int agreeCount;    //찬성 개수
	private int disagreeCount;    //반대 개수
	private Boolean myVote; //내 투표 상태

	private VoteSummaryResponse(int agreeCount, int disagreeCount, Boolean myVote) {
		this.agreeCount = agreeCount;
		this.disagreeCount = disagreeCount;
		this.myVote = myVote;
	}

	public static VoteSummaryResponse of(int agreeCount, int disagreeCount, Boolean myVote) {
		return new VoteSummaryResponse(agreeCount, disagreeCount, myVote);
	}
}
