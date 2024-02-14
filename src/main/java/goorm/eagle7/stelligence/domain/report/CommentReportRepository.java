package goorm.eagle7.stelligence.domain.report;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.eagle7.stelligence.domain.report.model.CommentReport;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {

	boolean existsByCommentIdAndReporterId(Long commentId, Long reporterId);

	// 신고자 ID로 신고한 댓글 신고 수 조회
	long countByReporterId(Long id);
}
