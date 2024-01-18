package goorm.eagle7.stelligence.domain.amendment.dto;

import static goorm.eagle7.stelligence.domain.document.content.dto.SectionResponseOuterClass.*;

import com.fasterxml.jackson.annotation.JsonInclude;

import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.amendment.model.AmendmentStatus;
import goorm.eagle7.stelligence.domain.amendment.model.AmendmentType;
import goorm.eagle7.stelligence.domain.section.model.Heading;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AmendmentResponse {

	private Long amendmentId;
	private String title;
	private String description;
	private AmendmentStatus status;
	private AmendmentType type;

	private SectionResponse targetSection;

	private Heading requestedSectionHeading;
	private String requestedSectionTitle;
	private String requestedSectionContent;

	private AmendmentResponse(Amendment amendment) {
		this.amendmentId = amendment.getId();
		this.title = amendment.getAmendmentTitle();
		this.description = amendment.getAmendmentDescription();
		this.status = amendment.getStatus();
		this.type = amendment.getType();
		this.targetSection = SectionResponse.of(amendment.getTargetSection());
		this.requestedSectionHeading = amendment.getNewSectionHeading();
		this.requestedSectionTitle = amendment.getNewSectionTitle();
		this.requestedSectionContent = amendment.getNewSectionContent();
	}

	public static AmendmentResponse of(Amendment amendment) {
		return new AmendmentResponse(amendment);
	}

}
