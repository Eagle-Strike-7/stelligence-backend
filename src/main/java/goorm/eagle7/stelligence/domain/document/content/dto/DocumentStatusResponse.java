package goorm.eagle7.stelligence.domain.document.content.dto;

import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 문서 상태 응답 DTO 입니다.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentStatusResponse {
	private Long documentId;
	private DocumentStatus documentStatus;    //문서 상태(편집가능, 투표중, 토론중, 토론참여자 대상 수정대기중)
	private Long contributeId;    //투표중인 상태일때 수정요청 정보
	private Long debateId;    //토론중, 토론참여자 수정대기중인 상태에서의 토론 정보

	public static DocumentStatusResponse of(Long documentId, Contribute latestContribute, Debate latestDebate) {
		if (latestContribute != null && latestContribute.isVoting()) {
			return new DocumentStatusResponse(
				documentId,
				DocumentStatus.VOTING,
				latestContribute.getId(),
				null
			);
		} else if (latestDebate != null && latestDebate.isOnDebate()) {
			return new DocumentStatusResponse(
				documentId,
				DocumentStatus.DEBATING,
				null,
				latestDebate.getId()
			);
		} else if (latestDebate != null && latestDebate.isPendingForContribute()) {
			return new DocumentStatusResponse(
				documentId,
				DocumentStatus.PENDING,
				null,
				latestDebate.getId()
			);
		} else {
			return new DocumentStatusResponse(
				documentId,
				DocumentStatus.EDITABLE,
				null,
				null
			);
		}
	}
}
