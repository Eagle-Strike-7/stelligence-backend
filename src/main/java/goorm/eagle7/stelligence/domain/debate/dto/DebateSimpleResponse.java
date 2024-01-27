package goorm.eagle7.stelligence.domain.debate.dto;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DebateSimpleResponse {

	private Long debateId;
	private LocalDateTime createdAt;
	private Long documentId;
	private String documentTitle;
	private String contributeTitle;
	private int commentsCount;

	private Long contributorId;
	private String contributorNickname;
}
