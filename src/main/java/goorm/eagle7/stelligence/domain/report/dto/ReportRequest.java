package goorm.eagle7.stelligence.domain.report.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class ReportRequest {

	private String description;

}
