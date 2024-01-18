package goorm.eagle7.stelligence.domain.amendment.custom;

import java.util.List;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.amendment.model.AmendmentStatus;
import goorm.eagle7.stelligence.domain.amendment.model.QAmendment;
import jakarta.persistence.EntityManager;

public class CustomAmendmentRepositoryImpl implements CustomAmendmentRepository {

	private final JPAQueryFactory queryFactory;

	public CustomAmendmentRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public List<Amendment> findAmendments(AmendmentStatus status, Long documentId, Long memberId) {
		QAmendment amendment = QAmendment.amendment;
		BooleanBuilder builder = new BooleanBuilder();

		if (memberId != null) {
			builder.and(amendment.member.id.eq(memberId));
		}

		if (documentId != null) {
			builder.and(amendment.targetSection.document.id.eq(documentId));
		}

		if (status != null) {
			builder.and(amendment.status.eq(status));
		}

		return queryFactory.selectFrom(amendment)
			.where(builder)
			.fetch();
	}
}
