package goorm.eagle7.stelligence.domain.debate.model.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import goorm.eagle7.stelligence.domain.debate.dto.DebateOrderCondition;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.model.DebateStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DebateRepositoryImpl implements DebateRepositoryCustom {

	@PersistenceContext
	private final EntityManager em;

	@Override
	public Page<Debate> findPageByStatusAndOrderCondition(
		DebateStatus status, DebateOrderCondition orderCondition, Pageable pageable) {

		return switch (orderCondition) {
			case LATEST -> findPageByStatusOrderByLatest(status, pageable);
			case RECENT -> findPageByStatusOrderByRecentComment(status, pageable);
		};

	}

	private Page<Debate> findPageByStatusOrderByLatest(DebateStatus status, Pageable pageable) {

		List<Debate> debatePageContent = em.createQuery(
				"select d from Debate d"
					+ " where d.status = :status"
					+ " order by d.createdAt desc", Debate.class)
			.setParameter("status", status)
			.setFirstResult((int)pageable.getOffset())
			.setMaxResults(pageable.getPageSize())
			.getResultList();

		TypedQuery<Long> countQuery = em.createQuery(
				"select count(d) from Debate d"
					+ " where d.status = :status", Long.class)
			.setParameter("status", status);

		return PageableExecutionUtils.getPage(debatePageContent, pageable, countQuery::getSingleResult);
	}

	private Page<Debate> findPageByStatusOrderByRecentComment(DebateStatus status, Pageable pageable) {

		List<Debate> debatePageContent = em.createQuery(
				"select d"
					+ " from (select c.debate.id as debateId, max(c.createdAt) as recentCommentedAt"
					+ "       from Comment c"
					+ "       group by c.debate) as rc"
					+ " right outer join Debate d"
					+ " on d.id = rc.debateId"
					+ " where d.status = :status"
					+ " order by rc.recentCommentedAt desc", Debate.class)
			.setParameter("status", status)
			.setFirstResult((int)pageable.getOffset())
			.setMaxResults(pageable.getPageSize())
			.getResultList();

		TypedQuery<Long> countQuery = em.createQuery(
				"select count(d) from Debate d"
					+ " where d.status = :status", Long.class)
			.setParameter("status", status);

		return PageableExecutionUtils.getPage(debatePageContent, pageable, countQuery::getSingleResult);
	}
}
