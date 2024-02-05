package goorm.eagle7.stelligence.domain.vote.custom;

import java.util.Objects;

import com.querydsl.jpa.impl.JPAQueryFactory;

import goorm.eagle7.stelligence.domain.vote.model.QVote;
import goorm.eagle7.stelligence.domain.vote.model.VoteSummary;
import jakarta.persistence.EntityManager;

public class VoteCustomRepositoryImpl implements VoteCustomRepository {

	private final JPAQueryFactory queryFactory;

	public VoteCustomRepositoryImpl(EntityManager entityManager) {
		this.queryFactory = new JPAQueryFactory(entityManager);
	}

	//찬성 표를 가져오는 메서드
	private Long getAgreeCount(Long contributeId) {
		QVote vote = QVote.vote;

		return queryFactory
			.select(vote.count())
			.from(vote)
			.where(vote.contribute.id.eq(contributeId), vote.agree.isTrue())
			.fetchOne();
	}

	//전체 표를 가져오는 메서드
	private Long getTotalVotes(Long contributeId) {
		QVote vote = QVote.vote;

		return queryFactory
			.select(vote.count())
			.from(vote)
			.where(vote.contribute.id.eq(contributeId), vote.agree.isNotNull())
			.fetchOne();
	}

	@Override
	public VoteSummary getVoteSummary(Long contributeId) {

		Long agreements = getAgreeCount(contributeId);
		Long disagreements = getTotalVotes(contributeId) - agreements; //반대 표

		return new VoteSummary(Objects.requireNonNull(agreements).intValue(),
			Objects.requireNonNull(disagreements).intValue());
	}
}
