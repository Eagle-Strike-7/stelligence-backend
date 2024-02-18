package goorm.eagle7.stelligence.domain.vote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "생성할 투표 정보")
public class VoteRequest {

	@Schema(description = "투표할 수정요청의 ID", example = "1")
	@NotNull(message = "수정요청 ID를 입력해주세요.")
	private Long contributeId;

	@Schema(description = "수정요청에 대한 사용자의 투표 상태", example = "true")
	private Boolean agree;
}
