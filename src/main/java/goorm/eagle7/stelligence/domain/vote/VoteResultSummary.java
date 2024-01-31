package goorm.eagle7.stelligence.domain.vote;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * VoteSummary
 * 투표 결과를 나타내는 클래스입니다.
 */
@AllArgsConstructor
@Getter
public class VoteResultSummary {

	private Long contributeId;
	private int totalVotes; // 총 투표 수
	private int agreements; // 찬성 투표 수
}