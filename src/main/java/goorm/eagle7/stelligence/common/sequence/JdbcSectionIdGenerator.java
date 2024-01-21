package goorm.eagle7.stelligence.common.sequence;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * SectionId의 Sequence를 관리합니다.
 */
@Repository
@RequiredArgsConstructor
class JdbcSectionIdGenerator implements SectionIdGenerator {

	private final JdbcTemplate jdbcTemplate;

	/**
	 * sequence_table의 sequence_name이 section인 row의 sequence_value를 가져옵니다.
	 * for update를 사용하여 동시성 문제를 해결합니다.
	 */
	private static final String GET_SECTION_ID_SEQ = "SELECT sequence_value FROM sequence_table where sequence_name = 'section' for update";

	/**
	 * sequence_table의 sequence_name이 section인 row의 sequence_value를 1 증가시킵니다.
	 */
	private static final String UPDATE_SECTION_ID_SEQ = "UPDATE sequence_table SET sequence_value = sequence_value + 1 WHERE sequence_name = 'section'";

	/**
	 * Document가 다음으로 생성할 SectionId를 가져옵니다.
	 * 동시성 문제를 해결하기 위해, for update를 사용합니다.
	 * for update를 사용하므로 트랜잭션이 길어진다면 성능에 영향을 줄 수 있습니다.
	 * 따라서 새로운 트랜잭션을 생성하여 사용합니다.
	 *
	 * @return Auto Increment된 SectionId
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Long getAndIncrementSectionId() {

		Long sectionId = jdbcTemplate.queryForObject(GET_SECTION_ID_SEQ, Long.class);

		jdbcTemplate.update(UPDATE_SECTION_ID_SEQ);
		return sectionId;
	}

}
