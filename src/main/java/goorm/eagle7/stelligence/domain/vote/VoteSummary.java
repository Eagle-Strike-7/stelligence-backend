package goorm.eagle7.stelligence.domain.vote;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * VoteSummary
 * 투표 결과를 나타내는 클래스입니다.
 */
@AllArgsConstructor
@Getter
public class VoteSummary {

	private Long contributeId;
	private int totalVotes;
	private int agreements;
	private int disagreements;
}
