package goorm.eagle7.stelligence.domain.contribute;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import goorm.eagle7.stelligence.domain.contribute.custom.CustomContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;

public interface ContributeRepository extends JpaRepository<Contribute, Long>, CustomContributeRepository {
	@Query("SELECT c FROM Contribute c JOIN FETCH c.amendments JOIN FETCH c.member WHERE c.id = :id")
	Optional<Contribute> findByIdWithAmendmentsAndMember(@Param("id") Long id);
}
