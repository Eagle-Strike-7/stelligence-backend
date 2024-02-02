package goorm.eagle7.stelligence.domain.debate.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import goorm.eagle7.stelligence.domain.debate.model.Debate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DebatePageResponse {

	private List<DebateSimpleResponse> debates;
	private int totalPages;

	public static DebatePageResponse from(Page<Debate> debatePage) {

		return new DebatePageResponse(debatePage);
	}

	private DebatePageResponse(Page<Debate> debatePage) {
		this.debates = debatePage.getContent().stream().map(DebateSimpleResponse::from).toList();
		this.totalPages = debatePage.getTotalPages();
	}
}
