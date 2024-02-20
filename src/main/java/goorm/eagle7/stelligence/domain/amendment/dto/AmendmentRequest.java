package goorm.eagle7.stelligence.domain.amendment.dto;

import goorm.eagle7.stelligence.domain.amendment.model.AmendmentType;
import goorm.eagle7.stelligence.domain.section.model.Heading;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AmendmentRequest {
	private Long sectionId;

	@NotNull(message = "수정안의 유형을 입력해주세요.")
	private AmendmentType type;       // 수정안의 유형 (CREATE, DELETE, UPDATE)

	@NotNull(message = "섹션의 제목 수준을 입력해주세요.")
	private Heading newSectionHeading;
	
	private String newSectionTitle;

	private String newSectionContent;

	private Integer creatingOrder;
}
