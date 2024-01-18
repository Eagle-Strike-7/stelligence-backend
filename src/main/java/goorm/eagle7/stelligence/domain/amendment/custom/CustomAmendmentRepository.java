package goorm.eagle7.stelligence.domain.amendment.custom;

import java.util.List;

import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.amendment.model.AmendmentStatus;

public interface CustomAmendmentRepository {
	List<Amendment> findAmendments(AmendmentStatus status, Long documentId, Long memberId);
}
