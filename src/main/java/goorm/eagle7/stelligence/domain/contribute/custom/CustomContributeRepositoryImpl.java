package goorm.eagle7.stelligence.domain.contribute.custom;

import java.util.List;

import org.springframework.data.domain.Pageable;

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
	public List<Contribute> findContributesByDocument(Long documentId, Pageable pageable) {

		QContribute contribute = QContribute.contribute;

		return queryFactory.selectFrom(contribute)
			.where(contribute.document.id.eq(documentId))
			.orderBy(contribute.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	@Override
	public List<Contribute> findVotingContributes(Pageable pageable) {
		QContribute contribute = QContribute.contribute;

		return queryFactory.selectFrom(contribute)
			.where(contribute.status.eq(ContributeStatus.VOTING))
			.orderBy(contribute.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}
}
