package goorm.eagle7.stelligence.domain.document.content.dto;

import java.time.LocalDateTime;
import java.util.List;

import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.document.content.parser.SectionResponseConcatenator;
import goorm.eagle7.stelligence.domain.member.dto.MemberSimpleResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Document 응답 DTO 입니다.
 * Document의 정보와 Section의 정보를 담습니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentResponse {

	private Long documentId;
	private String title;

	private Long latestRevision;
	private Long currentRevision;

	// 최종 수정 일시 (latestRevision 기준)
	private LocalDateTime lastModifiedAt;

	private List<SectionResponse> sections;

	/**
	 * Document의 모든 섹션의 내용을 하나의 문자열로 합친 내용입니다.
	 * 사용자가 글 조회시 프론트엔드에서 섹션의 내용을 하나의 문자열로 합쳐서 보여주기 편리하게 만듦니다.
	 *
	 * sections에서 파생되는 정보기 때문에 캐싱의 대상으로 삼지 않습니다.
	 * 매번 요청시 새로 계산해서 반환합니다.
	 */
	private String content;

	// 최초 기여자
	private MemberSimpleResponse originalAuthor;

	private List<MemberSimpleResponse> contributors;

	private DocumentStatus documentStatus;	//문서 상태(편집가능, 투표중, 토론중, 토론참여자 대상 수정대기중)
	private Long contributeId;	//투표중인 상태일때 수정요청 정보
	private Long debateId;	//토론중, 토론참여자 수정대기중인 상태에서의 토론 정보

	/**
	 * DocumentResponse를 생성합니다.
	 *
	 * DocumentResponse의 sections는 해당 Document와 연결된 모든 Section을 담지 않습니다.
	 * DocumentResponse에 담길 sections는 특정 버전에 해당하는 섹션만 담기므로, DTO 외부에서 이를 결정하고 삽입해주도록 했습니다. (documentContentService.getDocument 참조)
	 * @param document : 조회한 Document
	 * @param sections : 특정 버전에 해당하는 섹션들
	 * @return 생성된 DocumentResponse
	 */
	public static DocumentResponse of(
		Document document,
		Long currentRevision,
		List<SectionResponse> sections,
		List<MemberSimpleResponse> contributors,
		Contribute latestContribute,
		Debate latestDebate
	) {
		DocumentStatusInfo documentStatusInfo = DocumentStatusInfo.of(latestContribute, latestDebate);

		return new DocumentResponse(
			document.getId(),
			document.getTitle(),
			document.getLatestRevision(),
			currentRevision,
			document.getUpdatedAt(),
			sections,
			SectionResponseConcatenator.concat(sections),
			MemberSimpleResponse.from(document.getAuthor()),
			contributors,
			documentStatusInfo.getDocumentStatus(),
			documentStatusInfo.getContributeId(),
			documentStatusInfo.getDebateId()

		);
	}

	/**
	 * DocumentStatus와 관련된 정보를 처리하는 내부 정적 클래스입니다.
	 */
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	private static class DocumentStatusInfo {
		private DocumentStatus documentStatus;	//문서 상태(편집가능, 투표중, 토론중, 토론참여자 수정대기중)
		private Long contributeId;	//투표중인 상태일때 수정요청 정보
		private Long debateId;	//토론중, 토론참여자 수정대기중인 상태에서의 토론 정보

		public static DocumentStatusInfo of(Contribute latestContribute, Debate latestDebate) {
			if (latestContribute != null && latestContribute.isVoting()) {
				return new DocumentStatusInfo(DocumentStatus.VOTING, latestContribute.getId(), null);
			} else if (latestDebate != null && latestDebate.isOnDebate()) {
				return new DocumentStatusInfo(DocumentStatus.DEBATING, null, latestDebate.getId());
			} else if (latestDebate != null && latestDebate.isPendingForContribute()) {
				return new DocumentStatusInfo(DocumentStatus.PENDING, null, latestDebate.getId());
			} else {
				return new DocumentStatusInfo(DocumentStatus.EDITABLE, null, null);
			}
		}
	}

}