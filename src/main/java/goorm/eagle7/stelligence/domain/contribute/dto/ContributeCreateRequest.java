package goorm.eagle7.stelligence.domain.contribute.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "of")
public class ContributeCreateRequest {

	private String title;
	private List<Long> amendmentIdList;

}