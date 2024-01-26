package goorm.eagle7.stelligence.domain.vote.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VoteRequest {

	private Long contributeId;

	private Short agree;
}
