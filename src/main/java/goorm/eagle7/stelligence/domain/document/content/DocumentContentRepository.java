package goorm.eagle7.stelligence.domain.document.content;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import goorm.eagle7.stelligence.domain.document.content.model.Document;
import jakarta.persistence.LockModeType;

public interface DocumentContentRepository extends JpaRepository<Document, Long> {

	/**
	 * Document를 조회합니다.
	 * 업데이트용 조회로, 조회된 Document는 Lock이 걸립니다.
	 * Contriute Merge를 하는 용도로만 한정적으로 사용하기로 합시다.
	 * @param documentId
	 * @return
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select d from Document d where d.id = :documentId")
	Optional<Document> findForUpdate(Long documentId);

	/**
	 * 특정 문자열을 포함하는 Document의 ID를 조회합니다. 최신 버전의 섹션만 조사의 대상이 됩니다.
	 * @param keyword
	 * @return
	 */
	@Query("select distinct d.id "
		+ "from Document d "
		+ "join Section s on s.document = d "
		+ "where s.revision = ("
		+ "   select max(s2.revision) "
		+ "   from Section s2 "
		+ "   where s2.id = s.id "
		+ "   and s2.revision <= d.currentRevision"
		+ ") "
		+ "and s.content like %:keyword%")
	List<Long> findDocumentIdWhichContainsKeywordInLatestVersion(String keyword);

}
