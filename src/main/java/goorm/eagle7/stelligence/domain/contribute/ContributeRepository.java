package goorm.eagle7.stelligence.domain.contribute;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import goorm.eagle7.stelligence.domain.contribute.custom.CustomContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.document.content.model.Document;

public interface ContributeRepository extends JpaRepository<Contribute, Long>, CustomContributeRepository {
	@Query("SELECT c FROM Contribute c LEFT JOIN FETCH c.amendments LEFT JOIN FETCH c.member WHERE c.id = :id")
	Optional<Contribute> findByIdWithAmendmentsAndMember(@Param("id") Long id);

	// 생성시간이 from과 to 사이인 Contribute를 가져온다.
	@Query("SELECT c FROM Contribute c LEFT JOIN FETCH c.amendments LEFT JOIN FETCH c.member WHERE c.createdAt BETWEEN :from AND :to AND c.status = 'VOTING'")
	List<Contribute> findByStatusIsVotingAndCreatedAtBetween(@Param("from") LocalDateTime from,
		@Param("to") LocalDateTime to);

	/**
	 * document와 status를 통해 Contribute가 존재하는지 확인한다.
	 * @param document 문서
	 * @param status Contribute 상태
	 * @return 존재 여부
	 */
	boolean existsByDocumentAndStatus(Document document, ContributeStatus status);

	/**
	 * 현재 투표중인 수정요청에 대해 특정 제목으로 변경하고자 하는 요청이 존재하는지 확인한다.
	 * @param title 검증할 문서 제목
	 * @return 존재 여부
	 */
	@Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END"
		+ " FROM Contribute c"
		+ " left join Debate d on d.contribute = c"
		+ " WHERE c.afterDocumentTitle = :title"
		+ " AND ("
		+ " c.status = goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus.VOTING"
		+ " OR d.status = goorm.eagle7.stelligence.domain.debate.model.DebateStatus.OPEN"
		+ " )")
	boolean existsDuplicateRequestedDocumentTitle(String title);

	/**
	 * member가 생성한 수정 요청의 총 개수를 가져온다.
	 * @param memberId member id
	 *  @return 수정 요청의 총 개수
	 */
	long countDistinctByMemberId(Long memberId);

	/**
	 * member가 생성한 수정 요청 중 상태에 따른 요청의 총 개수를 가져온다.
	 * @param memberId member id
	 *  @return merge된 수정 요청의 총 개수
	 */
	long countDistinctByMemberIdAndStatus(Long memberId, ContributeStatus status);
}
