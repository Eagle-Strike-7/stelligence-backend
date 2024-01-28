package goorm.eagle7.stelligence.domain.contribute.custom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import goorm.eagle7.stelligence.domain.contribute.model.Contribute;

public interface CustomContributeRepository {
	Page<Contribute> findContributesByDocument(Long documentId, Pageable pageable);

	Page<Contribute> findVotingContributes(Pageable pageable);
}
