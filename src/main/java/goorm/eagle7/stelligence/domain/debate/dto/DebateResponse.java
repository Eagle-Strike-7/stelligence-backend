package goorm.eagle7.stelligence.domain.debate.dto;

import java.time.LocalDateTime;

import goorm.eagle7.stelligence.domain.contribute.dto.ContributeResponse;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.model.DebateStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DebateResponse {

	// 토론 정보
	private Long debateId;
	private LocalDateTime createdAt;
	private LocalDateTime endAt;
	private DebateStatus status;

	private ContributeResponse contribute;

	public static DebateResponse of(Debate debate) {
		return new DebateResponse(debate);
	}

	private DebateResponse(Debate debate) {
		this.debateId = debate.getId();
		this.createdAt = debate.getCreatedAt();
		this.endAt = debate.getEndAt();
		this.status = debate.getStatus();

		this.contribute = ContributeResponse.of(debate.getContribute());
	}
}
