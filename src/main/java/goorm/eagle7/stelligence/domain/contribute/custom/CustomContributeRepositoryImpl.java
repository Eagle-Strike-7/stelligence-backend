package goorm.eagle7.stelligence.domain.contribute.custom;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.contribute.model.QContribute;
import jakarta.persistence.EntityManager;

public class CustomContributeRepositoryImpl implements CustomContributeRepository {

	private final JPAQueryFactory queryFactory;

	public CustomContributeRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public Page<Contribute> findContributesByDocument(Long documentId, Pageable pageable) {

		QContribute contribute = QContribute.contribute;

		// 결과를 가져오는 쿼리
		List<Contribute> contributes = queryFactory.selectFrom(contribute)
			.where(contribute.document.id.eq(documentId))
			.orderBy(contribute.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// 전체 개수를 계산하는 쿼리는 필요할 때만 실행
		JPAQuery<Long> countQuery = queryFactory.select(contribute.count())
			.from(contribute)
			.where(contribute.document.id.eq(documentId));

		// PageableExecutionUtils를 사용하여 Page 객체 생성
		return PageableExecutionUtils.getPage(contributes, pageable, countQuery::fetchOne);
	}

	@Override
	public Page<Contribute> findVotingContributes(Pageable pageable) {
		QContribute contribute = QContribute.contribute;

		List<Contribute> contributes = queryFactory.selectFrom(contribute)
			.where(contribute.status.eq(ContributeStatus.VOTING))
			.orderBy(contribute.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory.select(contribute.count())
			.from(contribute)
			.where(contribute.status.eq(ContributeStatus.VOTING));

		return PageableExecutionUtils.getPage(contributes, pageable, countQuery::fetchOne);
	}
}
