package goorm.eagle7.stelligence.domain.amendment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AmendmentDeleteSaveRequest {
	private String title;
	private String description;
	private Long sectionId;
}
