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
@DiscriminatorValue(value = ReportType.Types.COMMENT)
public class CommentReport extends Report {

	@Column(name = "comment_id")
	private Long commentId;

	private CommentReport(Long commentId, String description, Long reporterId) {
		super(description, reporterId);
		this.commentId = commentId;
	}

	public static CommentReport createCommentReport(Long commentId, String description, Long reporterId) {
		return new CommentReport(commentId, description, reporterId);
	}
}
