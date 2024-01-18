package goorm.eagle7.stelligence.domain.amendment.dto;

import goorm.eagle7.stelligence.domain.section.model.Heading;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AmendmentSaveCreateRequest { //수정안을 create로 생성할 때 dto
	private String title;
	private String description;
	private Long sectionId;
	private Heading heading;
	private String sectionTitle;
	private String sectionContent;
}
