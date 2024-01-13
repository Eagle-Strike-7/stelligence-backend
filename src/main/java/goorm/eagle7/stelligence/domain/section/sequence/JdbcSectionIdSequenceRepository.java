package goorm.eagle7.stelligence.domain.section.sequence;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * SectionIdCustomRepositoryImpl
 *
 * 복합키는 AUTO_INCREMENT를 적용할 수 없기 때문에, 별도의 sequence 테이블을 만들어서 관리합니다.
 * 현재 방법으로는 매번 가져올 때마다 update가 발생하여 성능에 문제가 있으므로, 추후 개선이 필요합니다.
 *
 * RequiresNew를 사용하여 트랜잭션의 생명주기를 별도로 관리하여,
 * 불필요하게 락 기간을 늘리지 않도록 합니다.
 */
@Repository
@RequiredArgsConstructor
public class JdbcSectionIdSequenceRepository implements SectionIdSequenceRepository {

	private final JdbcTemplate jdbcTemplate;

	private static final String GET_SECTION_ID_SEQ = "SELECT section_id_seq FROM section_id_sequence where document_id = ? for update";
	private static final String UPDATE_SECTION_ID_SEQ = "UPDATE section_id_sequence SET section_id_seq = section_id_seq + 1 WHERE document_id = ?";
	private static final String CREATE_SECTION_ID_SEQ = "INSERT INTO section_id_sequence (document_id, section_id_seq) VALUES (?, 1)";

	/**
	 * Document가 다음으로 생성할 SectionId를 가져옵니다.
	 * 동시성 문제를 해결하기 위해, for update를 사용합니다.
	 * @param documentId
	 * @return
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Long getAndIncrementSectionId(Long documentId) {

		Long sectionId = jdbcTemplate.queryForObject(GET_SECTION_ID_SEQ, Long.class, documentId);

		jdbcTemplate.update(UPDATE_SECTION_ID_SEQ, documentId);
		return sectionId;
	}

	/**
	 * Document의 SectionId Sequence를 생성합니다.
	 * @param documentId
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void createSequence(Long documentId) {
		jdbcTemplate.update(CREATE_SECTION_ID_SEQ, documentId);
	}
}
