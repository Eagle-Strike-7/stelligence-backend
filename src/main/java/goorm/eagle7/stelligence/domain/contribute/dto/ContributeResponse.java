package goorm.eagle7.stelligence.domain.contribute.dto;

import java.util.List;

import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentResponse;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.member.dto.MemberDetailResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContributeResponse {
	private Long contributeId;
	private String contributeTitle;
	private String contributeDescription;
	private Long documentId;
	private String documentTitle;
	private MemberDetailResponse contributor; //수정 요청한 멤버에 대한 정보
	private List<AmendmentResponse> amendments;

	// 추가된 필드
	private String newDocumentTitle;	//변경된 제목
	private Long existParentDocumentId;	//기존 상위 문서
	private String existParentDocumentTitle;
	private Long newParentDocumentId;	//변경된 상위 문서
	private String newParentDocumentTitle;

	private ContributeResponse(Contribute contribute) {
		this.contributeId = contribute.getId();
		this.contributeTitle = contribute.getTitle();
		this.contributeDescription = contribute.getDescription();
		this.documentId = contribute.getDocument().getId();
		this.documentTitle = contribute.getDocument().getTitle();
		this.contributor = MemberDetailResponse.from(contribute.getMember());
		this.amendments = contribute.getAmendments().stream()
			.map(AmendmentResponse::of)
			.toList();

		// 추가된 생성자
		this.newDocumentTitle = contribute.getNewDocumentTitle();
		this.existParentDocumentId = contribute.getDocument().getParentDocument() == null ?
			null : contribute.getDocument().getParentDocument().getId();
		this.existParentDocumentTitle = contribute.getDocument().getParentDocument() == null ?
			null : contribute.getDocument().getParentDocument().getTitle();
		this.newParentDocumentId = contribute.getNewParentDocument() == null ?
			null : contribute.getNewParentDocument().getId();
		this.newParentDocumentTitle = contribute.getNewParentDocument() == null ?
			null : contribute.getNewParentDocument().getTitle();

	}

	public static ContributeResponse of(Contribute contribute) {
		return new ContributeResponse(contribute);
	}
}
