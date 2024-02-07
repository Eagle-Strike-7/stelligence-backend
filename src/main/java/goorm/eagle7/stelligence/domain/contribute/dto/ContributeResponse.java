package goorm.eagle7.stelligence.domain.contribute.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentResponse;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.member.dto.MemberDetailResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContributeResponse {

	@Value("${contribute.scheduler.vote-expiration-minutes:30}")
	long voteExpirationMinutes;	// 투표 지속시간

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

	// 추가된 필드
	private ContributeStatus contributeStatus;	//현재 수정요청의 상태
	private String documentTitle;	//수정요청과 관련 없이 현재 제목
	private Long parentDocumentId;	//수정요청과 관련 없이 현재 상위 문서
	private String parentDocumentTitle;
	private Long relatedDebateId;	//연관된 토론
	private LocalDateTime endAt;

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

		// 추가된 필드
		this.contributeStatus = contribute.getStatus();
		this.documentTitle = contribute.getDocument().getTitle();
		this.parentDocumentId = contribute.getDocument().getParentDocument() == null ?
			null : contribute.getDocument().getParentDocument().getId();
		this.parentDocumentTitle = contribute.getDocument().getParentDocument() == null ?
			null : contribute.getDocument().getParentDocument().getTitle();
		this.relatedDebateId = contribute.getRelatedDebate().getId();
		this.endAt = contribute.getCreatedAt().plusMinutes(voteExpirationMinutes);
	}

	public static ContributeResponse of(Contribute contribute) {
		return new ContributeResponse(contribute);
	}
}
