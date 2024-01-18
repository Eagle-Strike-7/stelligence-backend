package goorm.eagle7.stelligence.domain.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.eagle7.stelligence.domain.member.model.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	/*
	 * member id로 member 정보를 가져온다.
	 * member가 있다면, Badge List와 Bookmark List를 함께 가져 옴.
	 * @Param("id") Member id
	 * @return Optional<Member>
	 */
	// @Query("SELECT m FROM Member m LEFT JOIN FETCH m.badges LEFT JOIN FETCH m.bookmarks WHERE m.id = :id")
	// Optional<Member> findByIdWithBadgesAndBookmarks(@Param("id") Long id);

	/*
	 * member nickname으로 member 정보를 가져온다.
	 * @Param("nickname") Member nickname
	 * @return Optional<Member>
	 */
	Optional<Member> findByNickname(String nickname);

	/**
	 * member nickname으로 member 정보가 존재하는지 확인한다.
	 * @param nickname	member nickname
	 * @return  존재하면 true, 존재하지 않으면 false
	 */
	boolean existsByNickname(String nickname);

}
