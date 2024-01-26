package goorm.eagle7.stelligence.domain.vote.custom;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;

import goorm.eagle7.stelligence.domain.vote.VoteSummary;
import goorm.eagle7.stelligence.domain.vote.model.QVote;
import goorm.eagle7.stelligence.domain.vote.model.Vote;
import jakarta.persistence.EntityManager;

public class VoteCustomRepositoryImpl implements VoteCustomRepository {

	private final JPAQueryFactory queryFactory;

	public VoteCustomRepositoryImpl(EntityManager entityManager) {
		this.queryFactory = new JPAQueryFactory(entityManager);
	}

	@Override
	public VoteSummary getVoteSummary(Long contributeId) {

		QVote vote = QVote.vote;

		// 투표 결과를 가져오는 쿼리
		List<Vote> totalVotesList = queryFactory
			.selectFrom(vote)
			.where(vote.contribute.id.eq(contributeId))
			.fetch();

		int totalVotes = totalVotesList.size();

		// 찬성 투표 수를 가져오는 쿼리
		List<Vote> agreementsList = queryFactory
			.selectFrom(vote)
			.where(vote.contribute.id.eq(contributeId), vote.agree.eq((short)1))
			.fetch();

		int agreements = agreementsList.size();

		return new VoteSummary(contributeId, totalVotes, agreements);

	}
}
