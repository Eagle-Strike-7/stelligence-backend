package goorm.eagle7.stelligence.domain.amendment;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.eagle7.stelligence.domain.amendment.model.Amendment;

public interface AmendmentRepository extends JpaRepository<Amendment, Long> {
}
