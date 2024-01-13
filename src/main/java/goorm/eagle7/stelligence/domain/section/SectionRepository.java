package goorm.eagle7.stelligence.domain.section;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import goorm.eagle7.stelligence.domain.document.model.Document;
import goorm.eagle7.stelligence.domain.section.model.Section;
import goorm.eagle7.stelligence.domain.section.model.SectionId;

public interface SectionRepository extends JpaRepository<Section, SectionId> {

	/**
	 * Document의 특정 버전의 글을 조회하는 메서드입니다.
	 * @param document
	 * @param revision
	 * @return
	 */
	@Query("select s from Section s " +
		"where s.document = :document " +
		"and s.revision = (" +
		"   select max(s2.revision) " +
		"   from Section s2 " +
		"   where s2.id = s.id " +
		"   AND s2.revision <= :revision " +
		") ")
	List<Section> findByVersion(Document document, Long revision);

	/**
	 * 특정 SectionId에 대해 가장 최근에 개정된 버전을 가져옵니다.
	 * @param sectionId
	 * @return
	 */
	@Query("select s from Section s " +
		"where s.id = :sectionId " +
		"order by s.revision desc limit 1")
	Section findLatestSection(Long sectionId);

	/**
	 * Section이 중간에 삽입되는 경우
	 * 이후 순서인 섹션들의 순서를 변경하기 위한 메서드입니다.
	 * @param documentId
	 * @param revision
	 * @param insertOrders
	 * @return
	 */
	@Modifying
	@Query(nativeQuery = true,
		value = "UPDATE section " +
			"SET orders = (orders + 1) " +
			"WHERE document_id = :documentId " +
			"AND orders >= :insertOrders" +
			"  AND revision = ( " +
			"    SELECT max_revision FROM ( " +
			"                                 SELECT MAX(revision) AS max_revision " +
			"                                 FROM section AS s2 " +
			"                                 WHERE s2.section_id = section.section_id " +
			"                                   AND s2.revision <= :revision " +
			"                             ) AS subquery\n" +
			");")
	int updateOrders(Long documentId, Long revision, int insertOrders);

}
