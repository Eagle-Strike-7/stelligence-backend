package goorm.eagle7.stelligence.domain.graph.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "of")
public class DocumentGraphResponse {

	private List<DocumentNodeResponse> documentNodes;
	private List<HasChildRelationshipResponse> links;

}
