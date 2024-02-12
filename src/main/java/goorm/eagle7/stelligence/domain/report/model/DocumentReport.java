package goorm.eagle7.stelligence.domain.report.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue(value = ReportType.Types.DOCUMENT)
public class DocumentReport extends Report {

	@Column
	private Long documentId;

	public DocumentReport(Long documentId, String description, Long reporterId) {
		super(description, reporterId);
		this.documentId = documentId;
	}
}
