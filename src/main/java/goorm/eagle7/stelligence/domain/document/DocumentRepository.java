package goorm.eagle7.stelligence.domain.document;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import goorm.eagle7.stelligence.domain.document.model.Document;
import jakarta.persistence.LockModeType;

public interface DocumentRepository extends JpaRepository<Document, Long> {

	/**
	 * Document를 조회합니다.
	 * 업데이트용 조회로, 조회된 Document는 Lock이 걸립니다.
	 * @param documentId
	 * @return
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select d from Document d where d.id = :documentId")
	Optional<Document> findForUpdate(Long documentId);
}
