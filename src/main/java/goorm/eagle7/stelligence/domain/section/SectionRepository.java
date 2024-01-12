package goorm.eagle7.stelligence.domain.section;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.eagle7.stelligence.domain.section.model.Section;
import goorm.eagle7.stelligence.domain.section.model.SectionId;

public interface SectionRepository extends JpaRepository<Section, SectionId> {
}
