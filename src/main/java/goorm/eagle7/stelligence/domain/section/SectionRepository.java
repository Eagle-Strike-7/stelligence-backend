package goorm.eagle7.stelligence.domain.section;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.domain.document.content.model.Document;
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
		"and s.content is not null " +
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
	 * @return 최근 개정된 Section
	 */
	@Query("select s from Section s " +
		"where s.id = :sectionId " +
		"order by s.revision desc limit 1")
	Optional<Section> findLatestSection(Long sectionId);

	/**
	 * <h3>Section이 중간에 삽입되는 경우 이후 순서인 섹션들의 순서를 변경하기 위한 메서드입니다.</h3>
	 *
	 * <b>ISSUE : Modifying 쿼리와 영속성 컨텍스트의 초기화</b>
	 * <p>amendMergeTemplate에서 updateOrders를 수행할 때 flush와 clear 옵션을 준다면,
	 * 영속성 컨텍스트가 비워지고 해당 트랜잭션에서 사용하던 모든 객체가 detached 상태가 됩니다. 이로 인하여
	 * lazyloading이 필요한 객체들로부터 에러가 발생하며 이후 변경사항들을 감지하지 못하는 문제가 발생합니다.
	 *
	 * <p>구체적인 예는 다음과 같습니다.
	 * <ol>
	 *     <li>Amendment를 반영하는 for 루프에서, amendment를 반영하고 난 이후
	 *     다음 루프에서 lazyinitialization 에러가 발생합니다. 이는 Section을 생성하는 과정에서
	 *     document측 연관관계 매핑을 할 때 lazyloading이 발생하지만, document는 detached 상태이기 때문입니다.
	 *     </li>
	 *     <li>
	 *         Amendment를 반영하는 for 루프가 끝나고, document의 currentRevision을 증가하고
	 *         contribute의 상태를 merged로 변경하는 작업이 수행되지 않는데, 이는 두 객체가
	 *         detached이므로 변경감지의 대상이 되지 않기 때문입니다.
	 *     </li>
	 * </ol>
	 *
	 * <p>이를 해결하기 위해서는 updateOrders를 수행하는 트랜잭션에서 이 메서드가 호출된 이후 매번
	 * 사용하고자 하는 엔티티들에 대해 영속성 컨텍스트에 병합하는 과정을 거쳐야 합니다.
	 *
	 * <p>이 방법은 번거롭다고 판단하여 @Modifying 대신 변경감지를 통해 순서를 변경하도록 하였습니다. 대신 이 상황에서는
	 * 수정 대상 버전을 갖는 모든 Section을 메모리에 올려야하므로 메모리 사용량이 증가할 수 있습니다. 따라서 메모리 사용량을 줄이기 위해
	 * Section의 Content와 같은 필드들은 새로운 테이블로 분리하여 LazyLoading의 대상이 되게 할 수 있을 것입니다.
	 * 위 방법은 추후 고려해볼 예정이고, 일단은 기존의 엔티티를 사용하면서 변경감지를 통해 값을 수정하게 만들었습니다. 이러한 이유로
	 * 아래 메서드는 사용하지 않습니다.
	 *
	 * @param documentId 문서 ID
	 * @param revision 순서를 변경할 문서의 버전
	 * @param insertOrders 순서를 변경할 기준 순서
	 * @return 변경된 섹션의 개수
	 */
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Transactional
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
