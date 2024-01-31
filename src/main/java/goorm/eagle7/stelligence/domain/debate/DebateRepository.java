package goorm.eagle7.stelligence.domain.debate;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import goorm.eagle7.stelligence.domain.debate.model.Debate;

public interface DebateRepository extends JpaRepository<Debate, Long> {

	@Query("select d from Debate d"
		+ " join fetch d.contribute c"
		+ " join fetch c.member m"
		+ " join fetch c.amendments a"
		+ " where d.id = :debateId")
	Optional<Debate> findByIdWithContribute(@Param("debateId") Long debateId);
}
