package goorm.eagle7.stelligence.domain.contribute.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContributePageResponse {

	private List<ContributeListResponse> contributes;
	private int totalPages;
	private int totalElements;
	private int size;
	private int currentPage;
	private boolean isFirstPage;
	private boolean isLastPage;

	public static ContributePageResponse from(Page<ContributeListResponse> contributePage) {

		return new ContributePageResponse(contributePage);
	}

	private ContributePageResponse(Page<ContributeListResponse> contributePage) {
		this.contributes = contributePage.getContent();
		this.totalPages = contributePage.getTotalPages();
		this.totalElements = (int)contributePage.getTotalElements();
		this.size = contributePage.getSize();
		this.currentPage = contributePage.getNumber();
		this.isFirstPage = contributePage.isFirst();
		this.isLastPage = contributePage.isLast();
	}
}
