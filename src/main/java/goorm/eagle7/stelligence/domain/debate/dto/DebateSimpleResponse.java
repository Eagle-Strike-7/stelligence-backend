package goorm.eagle7.stelligence.domain.debate.dto;

import java.time.LocalDateTime;

import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.member.dto.MemberSimpleResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DebateSimpleResponse {

	// 토론 정보
	private Long debateId;
	private LocalDateTime createdAt;
	private LocalDateTime endAt;

	// 문서 정보
	private Long documentId;
	private String documentTitle;

	// 수정요청 정보
	private Long contributeId;
	private String contributeTitle;

	// 댓글 정보
	private int commentsCount;

	// 수정요청자 정보
	private MemberSimpleResponse contributor;

	public static DebateSimpleResponse from(Debate debate) {
		return new DebateSimpleResponse(debate);
	}

	private DebateSimpleResponse(Debate debate) {
		this.debateId = debate.getId();
		this.createdAt = debate.getCreatedAt();
		this.endAt = debate.getEndAt();

		this.documentId = debate.getContribute().getDocument().getId();
		this.documentTitle = debate.getContribute().getDocument().getTitle();

		this.contributeId = debate.getContribute().getId();
		this.contributeTitle = debate.getContribute().getTitle();

		this.commentsCount = debate.getComments().size();
		this.contributor = MemberSimpleResponse.from(debate.getContribute().getMember());
	}
}
