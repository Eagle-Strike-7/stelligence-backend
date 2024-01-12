package goorm.eagle7.stelligence.domain.section.sequence;

import static lombok.AccessLevel.*;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class SectionIdSequence {

	@Id
	private Long documentId;

	private Long sectionIdSeq;

	private SectionIdSequence(Long documentId) {
		this.documentId = documentId;
		this.sectionIdSeq = 0L;
	}

	public static SectionIdSequence createSequence(Long documentId) {
		return new SectionIdSequence(documentId);
	}

}
