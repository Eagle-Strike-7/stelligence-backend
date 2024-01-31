package goorm.eagle7.stelligence.domain.debate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.debate.dto.DebatePageResponse;
import goorm.eagle7.stelligence.domain.debate.dto.DebateResponse;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.model.DebateStatus;
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

	/**
	 * 특정 토론을 ID로 찾아서 조회합니다.
	 * @param debateId: 조회할 토론의 ID
	 * @return DebateResponse: 조회된 토론 응답 DTO
	 */
	@Transactional(readOnly = true)
	public DebateResponse getDebateDetailById(Long debateId) {
		Debate findDebate = debateRepository.findByIdWithContribute(debateId)
			.orElseThrow(() -> new BaseException("존재하지 않는 토론에 대한 조회 요청입니다. Debate ID: " + debateId));
		return DebateResponse.of(findDebate);
	}

	/**
	 * 토론의 상태(OPEN / CLOSED)에 따라 토론 리스트를 페이징을 적용하여 조회합니다.
	 * @param status: 조회하려는 토론의 상태(OPEN / CLOSED)
	 * @param pageable: 조회하려는 토론의 페이지 정보
	 * @return DebatePageResponse: 조회된 토론 페이지 응답 DTO
	 */
	@Transactional(readOnly = true)
	public DebatePageResponse getDebatePage(DebateStatus status, Pageable pageable) {

		Page<Debate> debatePage = debateRepository.findPageByStatus(status, pageable);
		return DebatePageResponse.from(debatePage);
	}
}
