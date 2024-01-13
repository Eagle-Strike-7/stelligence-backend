package goorm.eagle7.stelligence.common.sequence;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class JdbcSectionIdGenerator implements SectionIdGenerator {

	private final JdbcTemplate jdbcTemplate;

	private static final String GET_SECTION_ID_SEQ = "SELECT sequence_value FROM sequence_table where sequence_name = 'section' for update";
	private static final String UPDATE_SECTION_ID_SEQ = "UPDATE sequence_table SET sequence_value = sequence_value + 1 WHERE sequence_name = 'section'";

	/**
	 * Document가 다음으로 생성할 SectionId를 가져옵니다.
	 * 동시성 문제를 해결하기 위해, for update를 사용합니다.
	 * @return
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Long getAndIncrementSectionId() {

		Long sectionId = jdbcTemplate.queryForObject(GET_SECTION_ID_SEQ, Long.class);

		jdbcTemplate.update(UPDATE_SECTION_ID_SEQ);
		return sectionId;
	}

}
