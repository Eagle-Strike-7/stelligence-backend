package goorm.eagle7.stelligence.domain.debate.dto;

import java.util.List;

import goorm.eagle7.stelligence.domain.comment.dto.CommentResponse;
import goorm.eagle7.stelligence.domain.contribute.dto.ContributeResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DebateResponse {

	private Long debateId;
	private ContributeResponse contributeResponse;
	private List<CommentResponse> comments;

}
