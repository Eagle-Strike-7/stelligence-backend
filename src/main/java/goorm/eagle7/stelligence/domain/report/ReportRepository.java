package goorm.eagle7.stelligence.domain.report;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.eagle7.stelligence.domain.report.model.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
