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
}
