package goorm.eagle7.stelligence.domain.contribute.dto;

import java.util.List;

import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "생성할 수정요청의 정보")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ContributeRequest {

	@NotBlank(message = "수정요청의 제목을 입력해주세요.")
	@Schema(description = "수정요청의 제목", example = "마리모는 식물이 아닌 동물입니다.")
	private String contributeTitle;

	@NotBlank(message = "수정요청의 설명을 입력해주세요.")
	@Schema(description = "수정요청의 설명", example = "마리모는 동물입니다. 동물을 식물이라고 부르는 것은 마리모에게 실례입니다.")
	private String contributeDescription;

	@Valid
	@Schema(description = "수정요청에 포함될 개별 수정안 목록")
	private List<AmendmentRequest> amendments;

	@NotNull(message = "수정요청을 생성할 문서의 ID를 입력해주세요.")
	@Schema(description = "수정요청을 생성할 문서의 ID", example = "1")
	private Long documentId;

	@NotBlank(message = "문서의 변경될 제목을 입력해주세요.")
	@Schema(description = "문서의 변경될 제목", example = "마리모")
	private String afterDocumentTitle;

	@Schema(description = "문서의 변경될 부모 문서 ID", example = "2")
	private Long afterParentDocumentId;

	@Schema(description = "이 수정요청과 연관된 토론 ID", example = "1")
	private Long relatedDebateId;

	@Override
	public String toString() {
		return "ContributeRequest{"
			+ "contributeTitle='" + contributeTitle + '\''
			+ ", contributeDescription='" + contributeDescription + '\''
			+ ", amendments=" + amendments
			+ ", documentId=" + documentId
			+ ", afterDocumentTitle='" + afterDocumentTitle + '\''
			+ ", afterParentDocumentId=" + afterParentDocumentId
			+ ", relatedDebateId=" + relatedDebateId
			+ '}';
	}
}
