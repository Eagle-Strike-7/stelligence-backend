package goorm.eagle7.stelligence.domain.document.content.dto;

import java.time.LocalDateTime;
import java.util.List;

import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.document.content.parser.SectionResponseConcatenator;
import goorm.eagle7.stelligence.domain.member.dto.MemberDetailResponse;
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

	// 최종 수정 일시
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
	private MemberDetailResponse originalAuthor;

	private List<MemberDetailResponse> contributors;

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
		List<SectionResponse> sections,
		List<MemberDetailResponse> contributors
	) {
		return new DocumentResponse(
			document.getId(),
			document.getTitle(),
			document.getUpdatedAt(),
			sections,
			SectionResponseConcatenator.concat(sections),
			MemberDetailResponse.from(document.getAuthor()),
			contributors
		);
	}

}