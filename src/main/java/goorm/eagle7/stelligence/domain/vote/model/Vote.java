package goorm.eagle7.stelligence.domain.vote.model;

import static lombok.AccessLevel.*;

import goorm.eagle7.stelligence.common.entity.BaseTimeEntity;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.member.model.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Vote extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "vote_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contribute_id")
	private Contribute contribute;

	private Short agree; // 찬성 여부 (1: 찬성, 0: 반대)

	private Vote(Member member, Contribute contribute, Short agree) {
		this.member = member;
		this.contribute = contribute;
		this.agree = agree;
	}

	public static Vote createVote(Member member, Contribute contribute, Short agree) {
		return new Vote(member, contribute, agree);
	}
}
