package goorm.eagle7.stelligence.domain.document.content.dto;

import java.util.List;

import goorm.eagle7.stelligence.domain.document.content.model.Document;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Document 응답 DTO 입니다.
 * Document의 정보와 Section의 정보를 담습니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentResponse {

	private Long documentId;
	private String title;
	private List<SectionResponse> sections;

	private DocumentResponse(Long documentId, String title, List<SectionResponse> sections) {
		this.documentId = documentId;
		this.title = title;
		this.sections = sections;
	}

	/**
	 * DocumentResponse를 생성합니다.
	 *
	 * DocumentResponse의 sections는 해당 Document와 연결된 모든 Section을 담지 않습니다.
	 * DocumentResponse에 담길 sections는 특정 버전에 해당하는 섹션만 담기므로, DTO 외부에서 이를 결정하고 삽입해주도록 했습니다. (documentContentService.getDocument 참조)
	 * @param document : 조회한 Document
	 * @param sections : 특정 버전에 해당하는 섹션들
	 * @return
	 */
	public static DocumentResponse of(Document document, List<SectionResponse> sections) {
		return new DocumentResponse(document.getId(), document.getTitle(), sections);
	}

}
