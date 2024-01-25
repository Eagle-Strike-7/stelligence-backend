package goorm.eagle7.stelligence.domain.contribute.scheduler;

import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.vote.VoteCustomRepository;
import goorm.eagle7.stelligence.domain.vote.VoteSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ContributionConditionChecker
 *
 * Contribute와 Vote 상태를 확인하여 병합, 토론, 반려를 결정합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContributeSchedulingActionDeterminer {

	private final VoteCustomRepository voteRepository;

	private static final double MERGE_RATE = 0.8;
	private static final double DEBATE_RATE = 0.3;

	/**
	 * Contribute의 투표 결과를 확인하여 병합, 토론, 반려를 결정합니다.
	 * 결정된 결과는 ContributeSchedulingAction으로 반환됩니다.
	 * @param contribute 투표 결과를 확인할 Contribute
	 * @return 병합, 토론, 반려 중 하나
	 */
	public ContributeSchedulingAction check(Contribute contribute) {

		//contribute의 vote 수를 가져온다.
		VoteSummary voteSummary = voteRepository.getVoteSummary(contribute.getId());

		double agreeRate = (double)voteSummary.getAgree() / voteSummary.getTotalVotes();
		log.debug("Contribute {}의 투표 결과 : {} / {} = {}", contribute.getId(), voteSummary.getAgree(),
			voteSummary.getTotalVotes(), agreeRate);

		// 투표 결과에 따라 병합, 토론, 반려를 결정한다.
		if (agreeRate >= MERGE_RATE) {
			return ContributeSchedulingAction.MERGE;
		} else if (agreeRate >= DEBATE_RATE) {
			return ContributeSchedulingAction.DEBATE;
		} else {
			return ContributeSchedulingAction.REJECT;
		}
	}

}
