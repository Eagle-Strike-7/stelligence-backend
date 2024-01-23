package goorm.eagle7.stelligence.domain.contribute.dto;

import java.util.List;

import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ContributeRequest {
	private String title;
	private String description;
	private List<AmendmentRequest> amendmentRequests;
	private Long documentId;
}
