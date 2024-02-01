package goorm.eagle7.stelligence.domain.debate;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.model.DebateStatus;
import jakarta.persistence.LockModeType;

public interface DebateRepository extends JpaRepository<Debate, Long> {

	@Query("select d from Debate d"
		+ " join fetch d.contribute c"
		+ " join fetch c.member m"
		+ " join fetch c.amendments a"
		+ " where d.id = :debateId")
	Optional<Debate> findByIdWithContribute(@Param("debateId") Long debateId);

	@Query(value = "select d from Debate d"
		+ " join fetch d.contribute c"
		+ " where d.status = :status"
		+ " order by d.createdAt desc",
		countQuery = "select count(d) from Debate d"
			+ " where d.status = :status")
	Page<Debate> findPageByStatus(@Param("status") DebateStatus status, Pageable pageable);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query(value = "select d from Debate d where d.id = :debateId")
	Optional<Debate> findDebateByIdForUpdate(@Param("debateId") Long debateId);
}
