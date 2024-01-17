package goorm.eagle7.stelligence.domain.amendment.dto;

import goorm.eagle7.stelligence.domain.section.model.Heading;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AmendmenCreateSavetRequest {
	private String title;
	private String description;
	private Long sectionId;
	private Heading heading;
	private String sectionTitle;
	private String sectionContent;
}
