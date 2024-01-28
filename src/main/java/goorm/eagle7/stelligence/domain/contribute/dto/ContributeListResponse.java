package goorm.eagle7.stelligence.domain.contribute.dto;

import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContributeListResponse {
	private Long contributeId;
	private String contributeTitle;
	private String contributeDescription;
	private ContributeStatus contributeStatus;
	private Long documentId;
	private String documentTitle;
	private Long contributorId;
	private String contributorNickname;

	private ContributeListResponse(Contribute contribute) {
		this.contributeId = contribute.getId();
		this.contributeTitle = contribute.getTitle();
		this.contributeDescription = contribute.getDescription();
		this.contributeStatus = contribute.getStatus();
		this.documentId = contribute.getDocument().getId();
		this.documentTitle = contribute.getDocument().getTitle();
		this.contributorId = contribute.getMember().getId();
		this.contributorNickname = contribute.getMember().getNickname();
	}

	public static ContributeListResponse of(Contribute contribute) {
		return new ContributeListResponse(contribute);
	}
}
