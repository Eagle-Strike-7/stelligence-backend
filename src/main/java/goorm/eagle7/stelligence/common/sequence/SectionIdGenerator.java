package goorm.eagle7.stelligence.common.sequence;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Document가 다음으로 생성할 SectionId를 가져옵니다.
 */
public interface SectionIdGenerator {
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	Long getAndIncrementSectionId();
}
