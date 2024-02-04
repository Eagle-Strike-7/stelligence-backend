package goorm.eagle7.stelligence.domain.debate.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.model.DebateStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DebatePageResponse {

	private List<DebateSimpleResponse> debates;
	private int totalPages;
	private boolean hasNext;
	private boolean hasPrevious;
	private int currentPage;
	private DebateStatus status;
	private DebateOrderCondition order;

	public static DebatePageResponse from(Page<Debate> debatePage, DebateStatus status, DebateOrderCondition orderCondition) {

		return new DebatePageResponse(debatePage, status, orderCondition);
	}

	private DebatePageResponse(Page<Debate> debatePage, DebateStatus status, DebateOrderCondition orderCondition) {
		this.debates = debatePage.getContent().stream().map(DebateSimpleResponse::from).toList();
		this.totalPages = debatePage.getTotalPages();
		this.currentPage = debatePage.getNumber();
		this.hasNext = debatePage.hasNext();
		this.hasPrevious = debatePage.hasPrevious();
		this.status = status;
		this.order = orderCondition;
	}
}
