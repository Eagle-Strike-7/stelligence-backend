package goorm.eagle7.stelligence.domain.debate.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DebatePageResponse {

	private List<DebateSimpleResponse> debates;
	private int totalPages;

}
