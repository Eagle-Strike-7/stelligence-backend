package goorm.eagle7.stelligence.domain.vote;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.vote.custom.CustomVoteRepository;
import goorm.eagle7.stelligence.domain.vote.model.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long>, CustomVoteRepository {
	Optional<Vote> findByMemberAndContribute(Member member, Contribute contribute);

	/**
	 * 특정 기여에 투표한 회원 목록 조회
	 * 추후 Member.isDelete 추가시 해당 회원은 제외하는 로직 추가 필요
	 */
	@Query("SELECT v.member.id FROM Vote v "
		+ "WHERE v.contribute.id = :contributeId ")
	Set<Long> findVoters(Long contributeId);
}
