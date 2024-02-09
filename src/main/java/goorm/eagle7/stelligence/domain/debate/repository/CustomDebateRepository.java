package goorm.eagle7.stelligence.domain.debate.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import goorm.eagle7.stelligence.domain.debate.dto.DebateOrderCondition;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.model.DebateStatus;

public interface CustomDebateRepository {

	Page<Debate> findPageByStatusAndOrderCondition(
		DebateStatus status, DebateOrderCondition orderCondition, Pageable pageable);

	Optional<Debate> findLatestDebateByDocumentId(Long documentId);
}
