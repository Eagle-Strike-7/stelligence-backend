package goorm.eagle7.stelligence.domain.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import goorm.eagle7.stelligence.domain.member.model.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	@Query("SELECT m FROM Member m LEFT JOIN FETCH m.badges LEFT JOIN FETCH m.bookmarks WHERE m.id = :id")
	Optional<Member> findByIdWithBadgesAndBookmarks(@Param("id") Long id);

}
