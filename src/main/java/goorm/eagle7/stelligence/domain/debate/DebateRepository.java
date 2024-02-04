package goorm.eagle7.stelligence.domain.debate;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.model.repository.DebateRepositoryCustom;
import jakarta.persistence.LockModeType;

public interface DebateRepository extends JpaRepository<Debate, Long>, DebateRepositoryCustom {

	@Query("select d from Debate d"
		+ " join fetch d.contribute c"
		+ " join fetch c.member m"
		+ " join fetch c.amendments a"
		+ " where d.id = :debateId")
	Optional<Debate> findByIdWithContribute(@Param("debateId") Long debateId);

	// @Query(value = "select d from Debate d"
	// 	+ " join fetch d.contribute c"
	// 	+ " where d.status = :status"
	// 	+ " order by d.createdAt desc",
	// 	countQuery = "select count(d) from Debate d"
	// 		+ " where d.status = :status")
	// Page<Debate> findPageByStatus(@Param("status") DebateStatus status, Pageable pageable);

	/**
	 * 토론의 sequence를 이용해 다음 댓글의 sequence를 얻기 위해 조회합니다.
	 * 여러 사용자가 동시에 댓글 작성 요청을 보낼 때 같은 sequence를 갖지 못하도록 lock을 건 상태에서 조회합니다.
	 * @param debateId: 조회하려는 토론의 ID
	 * @return Optional<Debate>
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query(value = "select d from Debate d where d.id = :debateId")
	Optional<Debate> findDebateByIdForUpdate(@Param("debateId") Long debateId);

	// @Query(value = "select d.* from debate d"
	// 	+ " left join (select c.debate_id as debate_id, max(c.created_at) as recent_comment_at from comment c group by c.debate_id) as rc"
	// 	+ " on d.debate_id = rc.debate_id"
	// 	+ " where d.status = :status"
	// 	+ " order by rc.recent_comment_at desc",
	// 	// countQuery = "select count(d) from Debate d where d.status = :status",
	// 	nativeQuery = true)
	// Page<Debate> findPageByStatusOrderByRecentComment(@Param("status") DebateStatus status, Pageable pageable);
}
