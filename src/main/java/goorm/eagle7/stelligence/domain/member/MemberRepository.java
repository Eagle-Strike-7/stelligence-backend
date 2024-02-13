package goorm.eagle7.stelligence.domain.member;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.member.model.SocialType;

public interface MemberRepository extends JpaRepository<Member, Long> {

	/**
	 * <h2>Member nickname으로 활성 Member를 찾는다.</h2>
	 * @param  nickname Member nickname
	 * @return Optional<Member> Member 정보
	 */
	Optional<Member> findByNicknameAndActiveTrue(String nickname);

	/**
	 * <h2>Member nickname으로 활성 member의 존재를 확인한다.</h2>
	 * @param nickname	member nickname
	 * @return  존재하면 true, 존재하지 않으면 false
	 */
	boolean existsByNicknameAndActiveTrue(String nickname);

	/**
	 * <h2>Member id로 active가 true인 Member를 찾는다.</h2>
	 * @param memberId 활성 Member id
	 * @return Optional<Member> 활성 회원
	 */
	Optional<Member> findByIdAndActiveTrue(Long memberId);

	/**
	 * <h2>Member id로 active가 true, createdAt 오늘로부터 1일 이내인 Member의 존재를 확인한다.</h2>
	 * @param memberId 활성 member id
	 * @return 존재하면 true, 존재하지 않으면 false
	 */
	boolean existsByIdAndActiveTrueAndCreatedAtGreaterThanEqual(Long memberId, LocalDateTime dateTime);

	/**
	 * <h2>Member socialType과 socialId로 활성 member 획득.</h2>
	 * @param socialType member socialType
	 * @param socialId member socialId
	 * @return Optional<Member> 활성 member 정보
	 */
	Optional<Member> findBySocialTypeAndSocialIdAndActiveTrue(SocialType socialType, String socialId);

	/**
	 * <h2>Member id로 활성 member의 존재를 확인한다.</h2>
	 * @param memberId member id
	 * @return 존재하면 true, 존재하지 않으면 false
	 */
	boolean existsByIdAndActiveTrue(Long memberId);
}
