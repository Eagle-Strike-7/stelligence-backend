package goorm.eagle7.stelligence.domain.amendment.dto;

import goorm.eagle7.stelligence.domain.section.model.Heading;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AmendmentUpdateRequest { //수정안 자체를 수정할 때 dto
	private String title;
	private String description;
	private Heading heading;
	private String sectionTitle;
	private String sectionContent;
}
