package goorm.eagle7.stelligence.domain.contribute.scheduler;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.debate.DebateRepository;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 투표가 종료된 Contribute에 대한 토론을 생성합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DebateHandler implements ContributeSchedulingActionHandler {

	private final ContributeRepository contributeRepository;
	private final DebateRepository debateRepository;

	/**
	 * 수정요청을 토론으로 전환합니다.
	 * 이때의 수정요청은 투표중인 상태여야합니다.
	 * @param contributeId: 토론으로 전환할 수정 요청의 ID
	 */
	@Override
	@Transactional
	public void handle(Long contributeId) {
		log.info("Contribute {} debate open", contributeId);
		Contribute contribute = contributeRepository.findById(contributeId).orElseThrow();
		Debate debate = Debate.openFrom(contribute);
		debateRepository.save(debate);
	}
}
