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
	private MemberDetailResponse contributor; //수정 요청한 멤버에 대한 정보
	private List<AmendmentResponse> amendments;

	// 추가된 필드
	private String beforeDocumentTitle;	//기존 제목
	private String afterDocumentTitle;	//변경된 제목
	private Long beforeParentDocumentId;	//기존 상위 문서
	private String beforeParentDocumentTitle;
	private Long afterParentDocumentId;	//변경된 상위 문서
	private String afterParentDocumentTitle;

	private ContributeResponse(Contribute contribute) {
		this.contributeId = contribute.getId();
		this.contributeTitle = contribute.getTitle();
		this.contributeDescription = contribute.getDescription();
		this.documentId = contribute.getDocument().getId();
		this.contributor = MemberDetailResponse.from(contribute.getMember());
		this.amendments = contribute.getAmendments().stream()
			.map(AmendmentResponse::of)
			.toList();

		// 추가된 생성자
		this.beforeDocumentTitle = contribute.getBeforeDocumentTitle();
		this.afterDocumentTitle = contribute.getAfterDocumentTitle();
		this.beforeParentDocumentId = contribute.getBeforeParentDocument() == null ?
			null : contribute.getBeforeParentDocument().getId();
		this.beforeParentDocumentTitle = contribute.getBeforeParentDocument() == null ?
			null : contribute.getBeforeParentDocument().getTitle();
		this.afterParentDocumentId = contribute.getAfterParentDocument() == null ?
			null : contribute.getAfterParentDocument().getId();
		this.afterParentDocumentTitle = contribute.getAfterParentDocument() == null ?
			null : contribute.getAfterParentDocument().getTitle();

	}

	public static ContributeResponse of(Contribute contribute) {
		return new ContributeResponse(contribute);
	}
}
