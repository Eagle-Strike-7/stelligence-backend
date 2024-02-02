package goorm.eagle7.stelligence.domain.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import goorm.eagle7.stelligence.domain.member.model.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

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


	/**
	 * member socialId로 member 정보를 가져온다.
	 * @param socialId	member socialId
	 * @return Optional<Member> member 정보
	 */
	Optional<Member> findBySocialId(String socialId);


}
