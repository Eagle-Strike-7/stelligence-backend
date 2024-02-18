package goorm.eagle7.stelligence.domain.contribute.custom;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import goorm.eagle7.stelligence.domain.contribute.dto.ContributeSimpleResponse;
import goorm.eagle7.stelligence.domain.contribute.dto.QContributeSimpleResponse;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.contribute.model.QContribute;
import goorm.eagle7.stelligence.domain.vote.model.QVote;
import jakarta.persistence.EntityManager;

public class CustomContributeRepositoryImpl implements CustomContributeRepository {

	private final JPAQueryFactory queryFactory;

	public CustomContributeRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	// BooleanBuilder를 사용하여 중복 코드를 줄임
	private Page<ContributeSimpleResponse> findSimpleContributePage(BooleanBuilder builder, Pageable pageable) {
		QContribute contribute = QContribute.contribute;
		QVote vote = QVote.vote;

		List<ContributeSimpleResponse> contributes = queryFactory.select(new QContributeSimpleResponse(contribute,
				getVoteCount(vote, true).as("agreeCount"),
				getVoteCount(vote, false).as("disagreeCount")))
			.from(vote)
			.rightJoin(vote.contribute, contribute)
			.join(contribute.member).fetchJoin()
			.join(contribute.document).fetchJoin()
			.where(builder)
			.groupBy(contribute)
			.orderBy(contribute.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(contribute.count())
			.from(contribute)
			.where(builder);

		return PageableExecutionUtils.getPage(contributes, pageable, countQuery::fetchOne);
	}

	private static NumberExpression<Integer> getVoteCount(QVote vote, boolean agree) {
		return new CaseBuilder()
			.when(vote.agree.eq(agree)).then(1)
			.otherwise(0).sum();
	}

	// ContributeStatus에 따라 Contribute 목록을 반환
	@Override
	public Page<ContributeSimpleResponse> findByContributeStatus(ContributeStatus status, Pageable pageable) {
		QContribute contribute = QContribute.contribute;
		BooleanBuilder builder = new BooleanBuilder();

		builder.and(contribute.status.eq(status));

		// PageableExecutionUtils를 사용하여 Page 객체 생성
		return findSimpleContributePage(builder, pageable);
	}

	// 투표 완료된 Contribute 목록을 반환
	@Override
	public Page<ContributeSimpleResponse> findCompleteContributes(Pageable pageable) {
		QContribute contribute = QContribute.contribute;
		BooleanBuilder builder = new BooleanBuilder();

		builder.and(contribute.status.ne(ContributeStatus.VOTING));

		return findSimpleContributePage(builder, pageable);
	}

	// 문서 ID와 merged에 따라 Contribute 목록을 반환
	@Override
	public Page<ContributeSimpleResponse> findCompleteContributesByDocumentAndIsMerged(Long documentId, boolean merged,
		Pageable pageable) {

		QContribute contribute = QContribute.contribute;
		BooleanBuilder builder = new BooleanBuilder();

		builder.and(contribute.document.id.eq(documentId));

		// merged가 true이면 MERGED 목록을, false이면 REJECTED or DEBATING 목록을 반환
		if (merged) {
			builder.and(contribute.status.eq(ContributeStatus.MERGED));
		} else {
			builder.and(contribute.status.in(ContributeStatus.REJECTED, ContributeStatus.DEBATING));
		}

		return findSimpleContributePage(builder, pageable);
	}

	@Override
	public Optional<Contribute> findLatestContributeByDocumentId(Long documentId) {
		QContribute contribute = QContribute.contribute;

		Contribute findContribute = queryFactory
			.selectFrom(contribute)
			.where(contribute.document.id.eq(documentId))
			.orderBy(contribute.createdAt.desc())
			.limit(1)
			.fetchOne();

		return Optional.ofNullable(findContribute);
	}
}
