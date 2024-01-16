package goorm.eagle7.stelligence.domain.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "생성할 문서의 정보")
public class DocumentCreateRequest {

	@Schema(description = "문서의 제목", example = "마리모")
	private String title;
	@Schema(description = "문서의 부모 문서 ID", example = "1")
	private Long parentDocumentId;
	@Schema(description = "문서의 내용", example = "# 마리모\n마리모는 조류의 일종이다.\n## 마리모의 생애\n마리모의 수명은 평균 1년이다.\n")
	private String content;
}
