package goorm.eagle7.stelligence.domain.contribute.dto;

import java.time.LocalDateTime;
import java.util.List;

import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentResponse;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.member.dto.MemberSimpleResponse;
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
	private MemberSimpleResponse contributor; //수정 요청한 멤버에 대한 정보
	private List<AmendmentResponse> amendments;

	private String beforeDocumentTitle;	//기존 제목
	private String afterDocumentTitle;	//변경된 제목
	private Long beforeParentDocumentId;	//기존 상위 문서
	private String beforeParentDocumentTitle;
	private Long afterParentDocumentId;	//변경된 상위 문서
	private String afterParentDocumentTitle;

	// 추가된 필드
	private ContributeStatus contributeStatus;	//현재 수정요청의 상태
	private String documentTitle;	//수정요청과 관련 없이 조회 시점에서의 문서 제목
	private Long parentDocumentId;	//수정요청과 관련 없이 조회 시점에서의 상위 문서
	private String parentDocumentTitle;
	private Long relatedDebateId;	//연관된 토론
	private LocalDateTime endAt;	//투표 종료 시간

	private ContributeResponse(Contribute contribute) {
		this.contributeId = contribute.getId();
		this.contributeTitle = contribute.getTitle();
		this.contributeDescription = contribute.getDescription();
		this.documentId = contribute.getDocument().getId();
		this.contributor = MemberSimpleResponse.from(contribute.getMember());
		this.amendments = contribute.getAmendments().stream()
			.map(AmendmentResponse::of)
			.toList();

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

		// 추가된 필드에 대한 생성자
		this.contributeStatus = contribute.getStatus();
		this.documentTitle = contribute.getDocument().getTitle();
		this.parentDocumentId = contribute.getDocument().getParentDocument() == null ?
			null : contribute.getDocument().getParentDocument().getId();
		this.parentDocumentTitle = contribute.getDocument().getParentDocument() == null ?
			null : contribute.getDocument().getParentDocument().getTitle();
		this.relatedDebateId = contribute.getRelatedDebate() == null ?
			null : contribute.getRelatedDebate().getId();
		this.endAt = contribute.getEndAt();
	}

	public static ContributeResponse of(Contribute contribute) {
		return new ContributeResponse(contribute);
	}
}
