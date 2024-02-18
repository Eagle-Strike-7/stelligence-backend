package goorm.eagle7.stelligence.domain.contribute.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContributeDocumentPageResponse { // 문서별 수정요청 목록 조회 시 응답

	private List<ContributeSimpleResponse> contributes;
	private int totalPages;
	private int totalElements;
	private int size;
	private int currentPage;
	private boolean isFirstPage;
	private boolean isLastPage;
	private Long documentId;
	private String documentTitle;

	public static ContributeDocumentPageResponse from(Page<ContributeSimpleResponse> contributePage) {

		return new ContributeDocumentPageResponse(contributePage);
	}

	private ContributeDocumentPageResponse(Page<ContributeSimpleResponse> contributePage) {
		this.contributes = contributePage.getContent();
		this.totalPages = contributePage.getTotalPages();
		this.totalElements = (int)contributePage.getTotalElements();
		this.size = contributePage.getSize();
		this.currentPage = contributePage.getNumber();
		this.isFirstPage = contributePage.isFirst();
		this.isLastPage = contributePage.isLast();
		this.documentId = contributePage.getContent().get(0).getDocumentId();
		this.documentTitle = contributePage.getContent().get(0).getDocumentTitle();
	}
}
