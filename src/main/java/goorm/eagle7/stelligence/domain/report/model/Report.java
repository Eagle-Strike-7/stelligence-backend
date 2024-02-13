package goorm.eagle7.stelligence.domain.report.model;

import goorm.eagle7.stelligence.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "report_type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Report extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "report_id")
	protected Long id;

	protected String description;

	@Enumerated(EnumType.STRING)
	protected ReportStatus status;

	protected Long reporterId;

	protected Report(String description, Long reporterId) {
		this.description = description;
		this.status = ReportStatus.SUBMITTED;
		this.reporterId = reporterId;
	}
}
