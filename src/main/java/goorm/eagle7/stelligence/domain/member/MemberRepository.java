package goorm.eagle7.stelligence.domain.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.eagle7.stelligence.domain.member.model.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	/**
	 * <h2>Member id로 member를 찾는다.</h2>
	 * <p>Member와 Badge List, Bookmark List를 Fetch Join으로 함께 가져 옴.</p>
	 * @param  id Member id
	 * @return Optional<Member> Member 정보
	 */
	// @Query("SELECT m FROM Member m LEFT JOIN FETCH m.badges LEFT JOIN FETCH m.bookmarks WHERE m.id = :id")
	// Optional<Member> findByIdWithBadgesAndBookmarks(@Param("id") Long id);

	/**
	 * <h2>Member nickname으로 Member를 찾는다.</h2>
	 * @param  nickname Member nickname
	 * @return Optional<Member> Member 정보
	 */
	Optional<Member> findByNickname(String nickname);

	/**
	 * <h2>Member nickname으로 member의 존재를 확인한다.</h2>
	 * @param nickname	member nickname
	 * @return  존재하면 true, 존재하지 않으면 false
	 */
	boolean existsByNickname(String nickname);

}
