package goorm.eagle7.stelligence.domain.document.dto;

import lombok.Getter;

@Getter
public class DocumentCreateRequest {

	private String title;
	private Long parentDocumentId;
	private String content;
}
