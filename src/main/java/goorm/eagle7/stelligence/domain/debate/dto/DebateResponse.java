package goorm.eagle7.stelligence.domain.debate.dto;

import java.time.LocalDateTime;
import java.util.List;

import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentResponse;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.model.DebateStatus;
import goorm.eagle7.stelligence.domain.member.dto.MemberSimpleResponse;
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

	// 문서 정보
	private Long documentId;
	private String documentTitle;

	// 수정요청자 정보
	private MemberSimpleResponse contributor;

	// 수정요청 정보
	private Long contributeId;
	private String contributeTitle;
	private String contributeDescription;

	// 수정안 정보
	private List<AmendmentResponse> amendments;

	// 이전/다음 토론 정보 (추후 요구사항에 따라 추가될 예정입니다.)
	private DebateSimpleResponse prevDebate;
	private DebateSimpleResponse nextDebate;

	public static DebateResponse of(Debate debate) {
		return new DebateResponse(debate);
	}

	private DebateResponse(Debate debate) {
		this.debateId = debate.getId();
		this.createdAt = debate.getCreatedAt();
		this.endAt = debate.getEndAt();
		this.status = debate.getStatus();

		this.documentId = debate.getContribute().getDocument().getId();
		this.documentTitle = debate.getContribute().getDocument().getTitle();

		this.contributor = MemberSimpleResponse.from(debate.getContribute().getMember());

		this.contributeId = debate.getContribute().getId();
		this.contributeTitle = debate.getContribute().getTitle();
		this.contributeDescription = debate.getContribute().getDescription();

		this.amendments = debate.getContribute().getAmendments().stream().map(AmendmentResponse::of).toList();
	}
}
