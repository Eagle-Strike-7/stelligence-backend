package goorm.eagle7.stelligence.domain.contribute.custom;

import java.util.List;

import org.springframework.data.domain.Pageable;

import goorm.eagle7.stelligence.domain.contribute.model.Contribute;

public interface CustomContributeRepository {
	List<Contribute> findContributesByDocument(Long documentId, Pageable pageable);

	List<Contribute> findVotingContributes(Pageable pageable);
}
