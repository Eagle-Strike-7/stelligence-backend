package goorm.eagle7.stelligence.domain.contribute;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.eagle7.stelligence.domain.contribute.custom.CustomContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;

public interface ContributeRepository extends JpaRepository<Contribute, Long>, CustomContributeRepository {
}
