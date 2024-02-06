package goorm.eagle7.stelligence.domain.contribute.custom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;

public interface CustomContributeRepository {

	Page<Contribute> findByContributeStatus(ContributeStatus status, Pageable pageable);

	Page<Contribute> findCompleteContributes(Pageable pageable);

	Page<Contribute> findByDocumentAndStatus(Long documentId, ContributeStatus status, Pageable pageable);

	Page<Contribute> findByDocumentAndNonMerged(Long documentId, Pageable pageable);
}
