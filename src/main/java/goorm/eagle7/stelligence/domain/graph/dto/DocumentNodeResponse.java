package goorm.eagle7.stelligence.domain.graph.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentNodeResponse {

	private Long documentId;
	private String title;
	private String group;

}
