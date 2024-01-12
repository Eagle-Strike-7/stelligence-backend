package goorm.eagle7.stelligence.domain.document;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.eagle7.stelligence.domain.document.model.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
