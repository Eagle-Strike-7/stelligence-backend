package goorm.eagle7.stelligence.common.sequence;

import static lombok.AccessLevel.*;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

/**
 * DB sequence Key를 관리하는 테이블입니다.
 */
@Entity
@NoArgsConstructor(access = PROTECTED)
public class SequenceTable {

	@Id
	private String sequenceName;

	private Long sequenceValue;

	public SequenceTable(String sequenceName) {
		this.sequenceName = sequenceName;
		this.sequenceValue = 1L;
	}

}
