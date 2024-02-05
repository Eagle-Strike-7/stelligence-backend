package goorm.eagle7.stelligence.domain.debate.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
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

	/**
	 * 토론 ID 리스트를 입력받아 모두 한번에 CLOSED 상태로 변경하기 위한 쿼리입니다.
	 * DebateScheduler에 의해 호출됩니다.
	 * @param debateIdList: CLOSED 상태로 변경할 토론 ID 리스트
	 */
	@Modifying
	@Query("update Debate d"
		+ " set d.status = goorm.eagle7.stelligence.domain.debate.model.DebateStatus.CLOSED"
		+ " where d.id in :debateIdList")
	void closeAllById(@Param("debateIdList") List<Long> debateIdList);

	/**
	 * 시간에 따라 종료되어야하는 토론의 ID를 조회합니다.
	 * DebateScheduler에 의해 호출됩니다.
	 * @param now: 조회하려는 기준 시간. LocalDateTime.now()로 입력할 것을 강력하게 권장합니다.
	 * @return List&lt;Long&gt;: 토론의 ID가 리스트 형태로 반환됩니다.
	 */
	@Query("select d.id from Debate d"
		+ " where d.status = goorm.eagle7.stelligence.domain.debate.model.DebateStatus.OPEN"
		+ " and d.endAt <= :now")
	List<Long> findOpenDebateIdByEndAt(@Param("now") LocalDateTime now);

}
