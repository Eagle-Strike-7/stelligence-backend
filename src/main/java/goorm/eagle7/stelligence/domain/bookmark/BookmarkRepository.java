package goorm.eagle7.stelligence.domain.bookmark;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import goorm.eagle7.stelligence.domain.bookmark.model.Bookmark;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

	/**
	 * <h2>북마크 존재 여부 조회</h2>
	 * <p> - memberId, documentId를 이용해 북마크 존재 여부 조회</p>
	 * @param memberId - 로그인한 사용자의 ID
	 * @param documentId - 북마크 조회할 문서의 ID
	 * @return boolean - true or false
	 */
	boolean existsByMemberIdAndDocumentId(Long memberId, Long documentId);

	/**
	 * <h2>사용자의 북마크 목록 조회</h2>
	 * <p> - memberId를 이용해 사용자의 북마크 목록을 조회, Slice로 반환</p>
	 * <p> - Slice는 count 쿼리 따로 안 나감.</p>
	 * <p> - join fetch를 사용해 document를 함께 조회</p>
	 * @param memberId - 로그인한 사용자의 ID
	 * @param pageable - 페이지 정보
	 * @return Slice<Bookmark> - 사용자의 북마크 목록
	 */
	@Query(
		value = "select b from Bookmark b join fetch b.document d where b.member.id = :memberId order by b.id asc")
	Slice<Bookmark> findSliceByMemberIdWithPageable(Long memberId, Pageable pageable);

	/**
	 * <h2>북마크 단건 조회</h2>
	 * <p> - memberId, documentId를 이용해 북마크 단건 조회</p>
	 * @param memberId - 로그인한 사용자의 ID
	 * @param documentId - 북마크 조회할 문서의 ID
	 * @return Optional<Bookmark> - Bookmark or null
	 */
	@Query(value = "select b from Bookmark b where b.member.id = :memberId and b.document.id = :documentId")
	Optional<Bookmark> findByMemberIdAndDocumentId(Long memberId, Long documentId);

	/**
	 * <h2>북마크 삭제</h2>
	 * <p> - memberId, documentId를 이용해 북마크 삭제</p>
	 * @param memberId - 로그인한 사용자의 ID
	 * @param documentId - 북마크 삭제할 문서의 ID
	 */
	@Modifying
	@Query(value = "delete from Bookmark b where b.member.id = :memberId and b.document.id = :documentId")
	void deleteByMemberIdAndDocumentId(Long memberId, Long documentId);

}
