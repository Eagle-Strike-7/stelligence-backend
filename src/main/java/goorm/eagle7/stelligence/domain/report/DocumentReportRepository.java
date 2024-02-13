package goorm.eagle7.stelligence.domain.report;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.eagle7.stelligence.domain.report.model.DocumentReport;

public interface DocumentReportRepository extends JpaRepository<DocumentReport, Long> {

	boolean existsByDocumentIdAndReporterId(Long documentId, Long reporterId);
}
