package goorm.eagle7.stelligence.domain.report;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.eagle7.stelligence.domain.report.model.DocumentReport;

public interface DocumentReportRepository extends JpaRepository<DocumentReport, Long> {

	boolean existsByDocumentIdAndReporterId(Long documentId, Long reporterId);

	// 신고자 ID로 신고한 문서 신고 수 조회
	long countByReporterId(Long id);

}
