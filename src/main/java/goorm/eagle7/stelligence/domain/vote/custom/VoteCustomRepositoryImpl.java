package goorm.eagle7.stelligence.domain.vote.custom;

import java.util.Objects;

import com.querydsl.jpa.impl.JPAQueryFactory;

import goorm.eagle7.stelligence.domain.vote.VoteSummary;
import goorm.eagle7.stelligence.domain.vote.model.QVote;
import jakarta.persistence.EntityManager;

public class VoteCustomRepositoryImpl implements VoteCustomRepository {

	private final JPAQueryFactory queryFactory;

	public VoteCustomRepositoryImpl(EntityManager entityManager) {
		this.queryFactory = new JPAQueryFactory(entityManager);
	}

	@Override
	public VoteSummary getVoteSummary(Long contributeId) {

		QVote vote = QVote.vote;

		// 총 투표 수를 가져오는 쿼리
		Long totalVotes = queryFactory
			.select(vote.count())
			.from(vote)
			.where(vote.contribute.id.eq(contributeId), vote.agree.isNotNull())
			.fetchOne();

		// 찬성 투표 수를 가져오는 쿼리
		Long agreements = queryFactory
			.select(vote.count())
			.from(vote)
			.where(vote.contribute.id.eq(contributeId), vote.agree.isTrue())
			.fetchOne();

		return new VoteSummary(contributeId, Objects.requireNonNull(totalVotes).intValue(),
			Objects.requireNonNull(agreements).intValue());
	}
}
