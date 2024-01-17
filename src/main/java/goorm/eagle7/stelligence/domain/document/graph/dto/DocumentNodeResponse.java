package goorm.eagle7.stelligence.domain.document.graph.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class DocumentNodeResponse {

	private Long documentId;
	private String title;
	private String group;

}
