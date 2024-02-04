package goorm.eagle7.stelligence.domain.debate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import goorm.eagle7.stelligence.domain.debate.model.Debate;
import jakarta.persistence.LockModeType;

public interface DebateRepository extends JpaRepository<Debate, Long>, CustomDebateRepository {

	@Query("select d from Debate d"
		+ " join fetch d.contribute c"
		+ " join fetch c.member m"
		+ " join fetch c.amendments a"
		+ " where d.id = :debateId")
	Optional<Debate> findByIdWithContribute(@Param("debateId") Long debateId);


	/**
	 * 토론의 sequence를 이용해 다음 댓글의 sequence를 얻기 위해 조회합니다.
	 * 여러 사용자가 동시에 댓글 작성 요청을 보낼 때 같은 sequence를 갖지 못하도록 lock을 건 상태에서 조회합니다.
	 * @param debateId: 조회하려는 토론의 ID
	 * @return Optional<Debate>
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query(value = "select d from Debate d where d.id = :debateId")
	Optional<Debate> findDebateByIdForUpdate(@Param("debateId") Long debateId);

}
