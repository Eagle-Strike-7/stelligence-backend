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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Debate extends BaseTimeEntity {

	//토론 연장 지속 시간: 60분 * 24시간 = 1일
	public static final Long DEBATE_EXTENSION_DURATION_MINUTE = 60L * 24L;
	//토론 최대 지속 시간: 60분 * 24시간 * 7일 = 7일
	public static final Long DEBATE_LIMIT_DURATION_MINUTE = 60L * 24L * 7L;
	//토론 이후 수정요청 대기 시간: 60분 * 24시간 = 1일
	public static final Long DEBATE_PENDING_DURATION_MINUTE = 60L * 24L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "debate_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
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
		this.endAt = LocalDateTime.now().plusMinutes(DEBATE_EXTENSION_DURATION_MINUTE);
		this.commentSequence = 1;
	}

	// 특정 수정 요청으로부터 토론을 개시합니다.
	public static Debate openFrom(Contribute contribute) {
		if (!ContributeStatus.VOTING.equals(contribute.getStatus())) {
			throw new IllegalStateException("투표 중인 수정요청만 토론으로 전환할 수 있습니다.");
		}
		return new Debate(contribute);
	}

	/**
	 * 댓글이 업데이트 될때, 토론 종료시간도 업데이트됩니다.
	 * 단, 토론을 유지할 수 있는 최대 기간은 {@link #DEBATE_LIMIT_DURATION_MINUTE}분 입니다.
	 */
	public void updateEndAt() {
		LocalDateTime extendEndAt = LocalDateTime.now().plusMinutes(DEBATE_EXTENSION_DURATION_MINUTE);
		LocalDateTime limitEndAt = this.createdAt.plusMinutes(DEBATE_LIMIT_DURATION_MINUTE);

		if (extendEndAt.isBefore(limitEndAt)) {
			this.endAt = extendEndAt;
		} else {
			this.endAt = limitEndAt;
		}
	}

	// commentSequence를 사용하고 나면 commentSequence가 자동으로 업데이트됩니다.
	public int getAndIncrementCommentSequence() {
		return commentSequence++;
	}

	public boolean hasPermissionToWriteDrivenContribute(Long memberId) {
		return comments
			.stream()
			.map(c -> c.getCommenter().getId())
			.anyMatch(commenterId -> commenterId.equals(memberId));
	}

	// 토론 중인지를 확인
	public boolean isOnDebate() {
		return this.status.equals(DebateStatus.OPEN);
	}

	// 토론이 끝난 후 수정요청 대기중인지를 확인
	public boolean isPendingForContribute() {
		// 토론이 끝난 후 DEBATE_PENDING_DURATION_MINUTE 분 동안 수정요청 대기상태
		LocalDateTime pendingLimitTime = this.endAt.plusMinutes(DEBATE_PENDING_DURATION_MINUTE);
		return this.status.equals(DebateStatus.CLOSED) && pendingLimitTime.isAfter(LocalDateTime.now());
	}
}
