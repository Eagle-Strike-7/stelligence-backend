package goorm.eagle7.stelligence.domain.graph.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HasChildRelationshipResponse {

	private Long linkId;
	private Long parentDocumentId;
	private Long childDocumentId;
}
