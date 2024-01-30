package goorm.eagle7.stelligence.domain.debate.dto;

import java.time.LocalDateTime;

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

	// 문서 정보
	private Long documentId;
	private String documentTitle;

	// 수정요청 정보
	private String contributeTitle;

	// 댓글 정보
	private int commentsCount;

	// 수정요청자 정보
	private MemberSimpleResponse contributor;
}
