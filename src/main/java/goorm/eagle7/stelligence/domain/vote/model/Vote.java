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

	private Boolean agree; //1: 찬성, 0: 반대, null: 취소

	private Vote(Member member, Contribute contribute, Boolean agree) {
		this.member = member;
		this.contribute = contribute;
		this.agree = agree;
	}

	public static Vote createVote(Member member, Contribute contribute, Boolean agree) {
		return new Vote(member, contribute, agree);
	}

	public void updateAgree(Boolean agree) {
		if (this.agree.equals(agree)) { //요청된 값이 이미 같은 값이면 취소
			this.agree = null;
		} else { //다른 값이면 그 값으로 변경
			this.agree = agree;
		}
	}
}
