package goorm.eagle7.stelligence.domain.contribute.dto;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;

import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.vote.model.VoteSummary;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContributeSimpleResponse {
	private Long contributeId;
	private String contributeTitle;
	private String contributeDescription;
	private ContributeStatus contributeStatus;
	private Long documentId;
	private String documentTitle;
	private Long contributorId;
	private String contributorNickname;
	private VoteSummary voteSummary;
	private LocalDateTime createdAt;

	@QueryProjection
	public ContributeSimpleResponse(Contribute contribute, int agreeCount, int disagreeCount) {
		this.contributeId = contribute.getId();
		this.contributeTitle = contribute.getTitle();
		this.contributeDescription = contribute.getDescription();
		this.contributeStatus = contribute.getStatus();
		this.documentId = contribute.getDocument().getId();
		this.documentTitle = contribute.getDocument().getTitle();
		this.contributorId = contribute.getMember().getId();
		this.contributorNickname = contribute.getMember().getNickname();
		this.voteSummary = new VoteSummary(agreeCount, disagreeCount);
		this.createdAt = contribute.getCreatedAt();
	}
}
