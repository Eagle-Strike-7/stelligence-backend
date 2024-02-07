package goorm.eagle7.stelligence.domain.contribute.custom;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.querydsl.core.BooleanBuilder;
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

	// BooleanBuilder를 사용하여 중복 코드를 줄임
	private Page<Contribute> findContributesByCondition(BooleanBuilder builder, Pageable pageable) {
		QContribute contribute = QContribute.contribute;

		// 결과를 가져오는 쿼리
		List<Contribute> contributes = queryFactory
			.selectFrom(contribute)
			.where(builder)
			.orderBy(contribute.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// 전체 개수를 계산하는 쿼리는 필요할 때만 실행
		JPAQuery<Long> countQuery = queryFactory
			.select(contribute.count())
			.from(contribute)
			.where(builder);

		return PageableExecutionUtils.getPage(contributes, pageable, countQuery::fetchOne);
	}

	// ContributeStatus에 따라 Contribute 목록을 반환
	@Override
	public Page<Contribute> findByContributeStatus(ContributeStatus status, Pageable pageable) {
		QContribute contribute = QContribute.contribute;
		BooleanBuilder builder = new BooleanBuilder();

		builder.and(contribute.status.eq(status));

		// PageableExecutionUtils를 사용하여 Page 객체 생성
		return findContributesByCondition(builder, pageable);
	}

	// 투표 완료된 Contribute 목록을 반환
	@Override
	public Page<Contribute> findCompleteContributes(Pageable pageable) {
		QContribute contribute = QContribute.contribute;
		BooleanBuilder builder = new BooleanBuilder();

		builder.and(contribute.status.ne(ContributeStatus.VOTING));

		return findContributesByCondition(builder, pageable);
	}

	// 문서 ID와 merged에 따라 Contribute 목록을 반환
	@Override
	public Page<Contribute> findByDocumentAndStatus(Long documentId, boolean merged,
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

		return findContributesByCondition(builder, pageable);
	}
}
