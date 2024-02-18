package goorm.eagle7.stelligence.domain.document.content.dto;

import goorm.eagle7.stelligence.domain.document.content.model.Document;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentSimpleResponse {

	private Long documentId;
	private String title;

	public static DocumentSimpleResponse from(Document document) {
		return new DocumentSimpleResponse(document.getId(), document.getTitle());
	}
}
