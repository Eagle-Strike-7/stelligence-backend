package goorm.eagle7.stelligence.domain.debate.dto;

import java.time.LocalDateTime;
import java.util.List;

import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentResponse;
import goorm.eagle7.stelligence.domain.comment.dto.CommentResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DebateResponse {

	private Long debateId;
	private LocalDateTime createdAt;

	private Long contributorId;
	private String contributorNickname;

	private Long documentId;
	private String documentTitle;

	private Long contributeId;
	private String contributeTitle;
	private String contributeDescription;

	private List<AmendmentResponse> amendments;

	private List<CommentResponse> comments;

	private DebateSimpleResponse prevDebate;
	private DebateSimpleResponse nextDebate;
}
