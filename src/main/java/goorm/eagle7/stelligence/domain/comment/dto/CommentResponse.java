package goorm.eagle7.stelligence.domain.comment.dto;

import java.time.LocalDateTime;

import goorm.eagle7.stelligence.domain.member.dto.MemberSimpleResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentResponse {

	private Long commentId;
	private LocalDateTime createdAt;
	private MemberSimpleResponse createdBy;

}
