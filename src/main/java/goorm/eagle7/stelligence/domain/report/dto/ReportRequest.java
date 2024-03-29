package goorm.eagle7.stelligence.domain.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "from")
@Schema(description = "신고 대상에 대한 부가 정보")
public class ReportRequest {

	@NotBlank(message = "신고 사유를 입력해주세요.")
	@Schema(description = "신고 사유", example = "욕설이 포함된 글입니다.")
	private String description;

}
