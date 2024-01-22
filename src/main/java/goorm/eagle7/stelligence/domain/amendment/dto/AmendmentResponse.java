package goorm.eagle7.stelligence.domain.amendment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.amendment.model.AmendmentType;
import goorm.eagle7.stelligence.domain.document.content.dto.SectionResponse;
import goorm.eagle7.stelligence.domain.section.model.Heading;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AmendmentResponse {

	private Long amendmentId;
	private Long contributeId;

	private AmendmentType type;

	private SectionResponse targetSection;

	private Heading requestedSectionHeading;
	private String requestedSectionTitle;
	private String requestedSectionContent;

	private Integer creatingOrder;

	private AmendmentResponse(Amendment amendment) {
		this.amendmentId = amendment.getId();
		this.contributeId = amendment.getContribute().getId();
		this.type = amendment.getType();
		this.targetSection = SectionResponse.of(amendment.getTargetSection());
		this.requestedSectionHeading = amendment.getNewSectionHeading();
		this.requestedSectionTitle = amendment.getNewSectionTitle();
		this.requestedSectionContent = amendment.getNewSectionContent();
		this.creatingOrder = amendment.getCreatingOrder();
	}

	public static AmendmentResponse of(Amendment amendment) {
		return new AmendmentResponse(amendment);
	}

}
