package goorm.eagle7.stelligence.domain.debate.model;

import goorm.eagle7.stelligence.common.entity.BaseTimeEntity;
import goorm.eagle7.stelligence.domain.member.model.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "debate_id")
	private Debate debate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "commenter_id")
	private Member commenter;

	private String content;

	@Column(name = "sequences")
	private int sequence;

	public static Comment createComment(String content, Debate debate, Member commenter) {
		return new Comment(content, debate, commenter);
	}

	public Comment(String content, Debate debate, Member commenter) {
		this.content = content;
		this.debate = debate;
		this.commenter = commenter;
		this.sequence = debate.getNextCommentSequence();
		debate.updateEndAt();
		debate.getComments().add(this);
	}

	// 해당 회원이 댓글을 수정하거나 삭제할 권한이 있는지를 확인
	public boolean hasPermissionToModify(Long memberId) {
		return this.commenter.getId().equals(memberId);
	}

	public void updateContentTo(String content) {
		this.content = content;
	}
}
