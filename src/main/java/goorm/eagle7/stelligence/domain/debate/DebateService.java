package goorm.eagle7.stelligence.domain.debate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DebateService {

	private final DebateRepository debateRepository;

	/**
	 * 수정요청을 토론으로 전환합니다.
	 * 이때의 수정요청은 투표중인 상태여야합니다.
	 * 수정요청의 스케쥴러에 의해서만 호출되어야하는 메서드입니다.
	 * @param contribute: 토론으로 전환할 수정 요청
	 */
	public void convertToDebate(Contribute contribute) {
		Debate debate = Debate.openFrom(contribute);
		debateRepository.save(debate);
	}

	/**
	 * 특정 토론을 ID로 찾아서 종료합니다.
	 * 토론의 스케쥴러에 의해서만 호출되어야하는 메서드입니다.
	 * @param debateId: 종료할 토론의 ID
	 */
	public void closeDebateById(Long debateId) {
		Debate targetDebate = debateRepository.findById(debateId)
			.orElseThrow(() -> new IllegalArgumentException("삭제하려는 토론이 존재하지 않습니다. Debate ID: " + debateId));

		targetDebate.close();
	}
}
