package goorm.eagle7.stelligence.common.sequence;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface SectionIdGenerator {
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	Long getAndIncrementSectionId();
}
