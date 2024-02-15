package goorm.eagle7.stelligence.domain.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import goorm.eagle7.stelligence.domain.report.model.DocumentReport;

public interface DocumentReportRepository extends JpaRepository<DocumentReport, Long> {

	boolean existsByDocumentIdAndReporterId(Long documentId, Long reporterId);

	// 신고자 ID로 신고한 문서 신고 수 조회
	long countByReporterId(Long id);

	// reportId로 reporterId 조회
	@Query("SELECT reporterId FROM DocumentReport WHERE id = :reportId")
	Long findReporterIdById(Long reportId);

}
