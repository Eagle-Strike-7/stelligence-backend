package goorm.eagle7.stelligence.domain.report;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.eagle7.stelligence.domain.report.model.CommentReport;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {

	boolean existsByCommentIdAndReporterId(Long commentId, Long reporterId);

	long countByReporterId(Long id);
}
