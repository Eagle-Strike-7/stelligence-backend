package goorm.eagle7.stelligence.domain.debate.model.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import goorm.eagle7.stelligence.domain.debate.dto.DebateOrderCondition;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.model.DebateStatus;

public interface DebateRepositoryCustom {

	Page<Debate> findPageByStatusAndOrderCondition(
		DebateStatus status, DebateOrderCondition orderCondition, Pageable pageable);
}
