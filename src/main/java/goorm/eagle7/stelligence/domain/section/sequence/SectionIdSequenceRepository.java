package goorm.eagle7.stelligence.domain.section.sequence;

public interface SectionIdSequenceRepository {

	Long getAndIncrementSectionId(Long documentId);

	void createSequence(Long documentId);
}
