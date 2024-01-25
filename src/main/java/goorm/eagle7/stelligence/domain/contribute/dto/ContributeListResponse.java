package goorm.eagle7.stelligence.domain.contribute.dto;

import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ContributeListResponse {
	private Long contributeId;
	private String title;
	private String description;
	private ContributeStatus contributeStatus;
	private Long documentId;
	private String documentTitle;
	private Long contributorId;
	private String contributorName;

	private ContributeListResponse(Contribute contribute) {
		this.contributeId = contribute.getId();
		this.title = contribute.getTitle();
		this.description = contribute.getDescription();
		this.contributeStatus = contribute.getStatus();
		this.documentId = contribute.getDocument().getId();
		this.documentTitle = contribute.getDocument().getTitle();
		this.contributorId = contribute.getMember().getId();
		this.contributorName = contribute.getMember().getName();
	}

	public static ContributeListResponse of(Contribute contribute) {
		return new ContributeListResponse(contribute);
	}
}
