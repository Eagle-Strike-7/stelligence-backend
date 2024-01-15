package goorm.eagle7.stelligence.domain.document.dto;

import java.util.List;

import goorm.eagle7.stelligence.domain.document.model.Document;
import lombok.Getter;

/**
 * Document 응답 DTO 입니다.
 * Document의 정보와 Section의 정보를 담습니다.
 */
@Getter
public class DocumentResponse {

	private Long documentId;
	private String title;
	private List<SectionResponse> sections;

	private DocumentResponse(Long documentId, String title, List<SectionResponse> sections) {
		this.documentId = documentId;
		this.title = title;
		this.sections = sections;
	}

	public static DocumentResponse of(Document document, List<SectionResponse> sections) {
		return new DocumentResponse(document.getId(), document.getTitle(), sections);
	}

}
