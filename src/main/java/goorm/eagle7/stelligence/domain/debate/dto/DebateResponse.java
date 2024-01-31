package goorm.eagle7.stelligence.domain.debate.dto;

import java.time.LocalDateTime;
import java.util.List;

import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentResponse;
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

	// 이전/다음 토론 정보
	private DebateSimpleResponse prevDebate;
	private DebateSimpleResponse nextDebate;
}
