package goorm.eagle7.stelligence.domain.vote.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * VoteSummary
 * 투표 현황을 나타내는 클래스입니다.
 */
@AllArgsConstructor
@Getter
public class VoteSummary {

	private int agreeCount;    //찬성 개수
	private int disagreeCount;    //반대 개수
}
