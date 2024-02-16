package goorm.eagle7.stelligence.domain.document.content;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.member.model.Member;
import jakarta.persistence.LockModeType;

public interface DocumentContentRepository extends JpaRepository<Document, Long> {

	/**
	 * Document를 조회합니다.
	 * 업데이트용 조회로, 조회된 Document는 Lock이 걸립니다.
	 * Contriute Merge를 하는 용도로만 한정적으로 사용하기로 합시다.
	 * @param documentId 조회할 Document의 ID
	 * @return 조회된 Document - Lock이 걸린 상태
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select d from Document d where d.id = :documentId")
	Optional<Document> findForUpdate(Long documentId);

	/**
	 * 특정 문자열을 포함하는 Document의 ID를 조회합니다. 최신 버전의 섹션만 조사의 대상이 됩니다.
	 * @param keyword 검색할 문자열
	 * @return 검색된 Document의 ID 목록
	 */
	@Query("select distinct d.id "
		+ "from Document d "
		+ "join Section s on s.document = d "
		+ "where s.revision = ("
		+ "   select max(s2.revision) "
		+ "   from Section s2 "
		+ "   where s2.id = s.id "
		+ "   and s2.revision <= d.latestRevision"
		+ ") "
		+ "and s.content like %:keyword%")
	List<Long> findDocumentIdWhichContainsKeywordInLatestVersion(String keyword);

	/**
	 * 특정 Document에 기여한 사용자들을 조회합니다.
	 * @param documentId 조회할 Document의 ID
	 * @return 기여한 사용자 목록
	 */
	@Query("select distinct m from Contribute c "
		+ "join c.member m "
		+ "join c.document d "
		+ "where d.id = :documentId "
		+ "and c.status = goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus.MERGED "
		+ "order by m.nickname")
	List<Member> findContributorsByDocumentId(Long documentId);

	/**
	 * 특정 제목을 가진 Document를 조회합니다.
	 * 애플리케이션 로직 상 제목은 유일해야 합니다.
	 * @param title 조회할 Document의 제목
	 * @return 존재 여부
	 */
	Optional<Document> findByTitle(String title);

	/**
	 * 특정 제목을 가진 Document가 존재하는지 조회합니다.
	 * @param title 조회할 Document의 제목
	 * @return 존재 여부
	 */
	boolean existsByTitle(String title);

	/**
	 * Member가 작성한 Document의 수를 조회합니다.
	 * @param memberId 조회할 MemberId
	 * @return 작성한 Document의 수
	 */
	long countByAuthor_Id(Long memberId);

	@Query("select d from Document d join fetch d.author where d.id = :documentId")
	Optional<Document> findByIdWithAuthor(Long documentId);

}
