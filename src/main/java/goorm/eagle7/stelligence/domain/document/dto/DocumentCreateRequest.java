package goorm.eagle7.stelligence.domain.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "생성할 문서의 정보")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "of")
public class DocumentCreateRequest {

	@Schema(description = "문서의 제목", example = "마리모")
	private String title;
	@Schema(description = "문서의 부모 문서 ID", example = "1")
	private Long parentDocumentId;
	@Schema(description = "문서의 내용",
		example = "<h1>마리모</h1>"
			+ "<p>마리모는 조류의 일종이다.</p>"
			+ "<h2>마리모의 생애</h2>"
			+ "<p>마리모의 수명은 평균 1년이다.</p>")
	private String content;

	@Override
	public String toString() {
		return "DocumentCreateRequest{" +
			"title='" + title + '\'' +
			", parentDocumentId=" + parentDocumentId +
			", content='" + content.replace("\n", "\\n") + '\'' +
			'}';
	}
}
