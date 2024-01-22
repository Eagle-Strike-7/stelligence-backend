package goorm.eagle7.stelligence.common.sequence;

import static lombok.AccessLevel.*;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

/**
 * DB sequence Key를 관리하는 테이블입니다.
 * Section이 생성되는 경우 sequenceName이 section인 레코드에서
 * sequenceValue를 가져와 PK로 사용합니다.
 * -------------------------------
 * | sequenceName | sequenceValue |
 * -------------------------------
 * | section      | 1             |
 * -------------------------------
 */
@Entity
@NoArgsConstructor(access = PROTECTED)
public class SequenceTable {

	/**
	 * sequenceName은 DB sequence의 이름입니다.
	 * ex. section, document, ...
	 */
	@Id
	private String sequenceName;

	/**
	 * sequenceValue는 다음에 생성될 sequence의 값입니다.
	 * ex. 1, 2, 3, ...
	 */
	private Long sequenceValue;

	public SequenceTable(String sequenceName) {
		this.sequenceName = sequenceName;
		this.sequenceValue = 1L;
	}

}
