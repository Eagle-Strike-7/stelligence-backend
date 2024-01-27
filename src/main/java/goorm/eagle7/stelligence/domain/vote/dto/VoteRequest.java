package goorm.eagle7.stelligence.domain.vote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "생성할 투표 정보")
public class VoteRequest {

	@Schema(description = "투표할 수정요청의 ID", example = "1")
	private Long contributeId;

	@Schema(description = "투표할 수정요청에 대한 동의 여부 (0: 반대 1: 찬성)", example = "1")
	private Short agree;
}
