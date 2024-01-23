package goorm.eagle7.stelligence.domain.contribute.dto;

import java.util.List;

import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentResponse;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.member.dto.MemberMyPageResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ContributeResponse {
	private Long contributeId;
	private String title;
	private String description;
	private Long documentId;
	private String documentTitle;
	private MemberMyPageResponse contributor; //수정 요청한 멤버에 대한 정보
	private List<AmendmentResponse> amendments;

	private ContributeResponse(Contribute contribute) {
		this.contributeId = contribute.getId();
		this.title = contribute.getTitle();
		this.description = contribute.getDescription();
		this.documentId = contribute.getDocument().getId();
		this.documentTitle = contribute.getDocument().getTitle();
		this.contributor = MemberMyPageResponse.from(contribute.getMember());
		this.amendments = contribute.getAmendments().stream()
			.map(AmendmentResponse::of)
			.toList();
	}

	public static ContributeResponse of(Contribute contribute) {
		return new ContributeResponse(contribute);
	}
}
