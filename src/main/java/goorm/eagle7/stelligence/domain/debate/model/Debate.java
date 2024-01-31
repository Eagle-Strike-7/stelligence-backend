package goorm.eagle7.stelligence.domain.debate.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import goorm.eagle7.stelligence.common.entity.BaseTimeEntity;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Debate extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "debate_id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contribute_id")
	private Contribute contribute;

	@Enumerated(EnumType.STRING)
	private DebateStatus status;

	// 종료 예정 시각 (종료된 토론이라면 실제 종료된 시각)
	private LocalDateTime endAt;

	// 댓글의 순서를 부여하기 위한 시퀀스
	private int commentSequence;

	@OneToMany(mappedBy = "debate")
	private List<Comment> comments = new ArrayList<>();

	// 수정 요청으로부터 토론을 생성합니다.
	private Debate(Contribute contribute) {
		contribute.setStatusDebating();
		this.contribute = contribute;
		this.status = DebateStatus.OPEN;
		this.endAt = LocalDateTime.now().plusDays(1L);
		this.commentSequence = 1;
	}

	// 특정 수정 요청으로부터 토론을 개시합니다.
	public static Debate openFrom(Contribute contribute) {
		if (!ContributeStatus.VOTING.equals(contribute.getStatus())) {
			throw new IllegalStateException("투표 중인 수정요청만 토론으로 전환할 수 있습니다.");
		}
		return new Debate(contribute);
	}

	// 특정 토론을 닫습니다.
	public void close() {
		// 이미 종료된 토론이라면 그대로 유지
		if (DebateStatus.CLOSED.equals(this.status)) {
			return;
		}
		this.status = DebateStatus.CLOSED;
		this.endAt = LocalDateTime.now();
	}
}
