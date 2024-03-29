package goorm.eagle7.stelligence.domain.member;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import goorm.eagle7.stelligence.domain.member.model.Member;

@DataJpaTest
class MemberRepositoryTest {

	@Autowired
	private MemberRepository memberRepository;

	private Optional<Member> member1;
	private Optional<Member> withdrawnMember1;

	@BeforeEach
	void setUp() {

		// 1~4 line: 회원
		member1 = memberRepository.findById(1L);

		// 5~7 line: 탈퇴한 회원
		withdrawnMember1 = memberRepository.findById(5L);

	}

	/**
	 * <h2>[정상] Member nickname으로 Member 찾기, 탈퇴 회원 포함 - findByNickname</h2>
	 * <p>결과: 활성 회원은 Member 반환, 탈퇴 회원은 empty 반환</p>
	 * <p>검증 방식: 반환값 확인</p>
	 */
	@Test
	@DisplayName("[정상] Member nickname으로 Member 찾기, 탈퇴 회원 포함 검증 - findByNickname")
	void findByNickname() {

		// given
		String memberNickname = member1.get().getNickname();
		String withdrawnMemberNickname = withdrawnMember1.get().getNickname();

		// when
		Optional<Member> member = memberRepository.findByNicknameAndActiveTrue(memberNickname);
		Optional<Member> withdrawnMember = memberRepository.findByNicknameAndActiveTrue(withdrawnMemberNickname);

		// then
		assertThat(member).isEqualTo(member1);
		assertThat(withdrawnMember).isEmpty();

	}

	/**
	 * <h2>[정상] 존재하지 않는 Member nickname으로 Member 찾기, 탈퇴 회원 포함 -findByNickname</h2>
	 * <p>결과: empty 반환</p>
	 */
	@Test
	@DisplayName("[정상] 존재하지 않는 Member nickname으로 Member 찾기 -findByNickname")
	void findByNicknameEx() {

		// when
		Optional<Member> member = memberRepository.findByNicknameAndActiveTrue("nonExistNickname");

		// then
		assertThat(member).isEmpty();

	}

	/**
	 * <h2>[정상] Member nickname으로 member 존재 확인, 탈퇴 회원 포함 - existsByNickname</h2>
	 * <p>결과: 활성 회원은 true 반환, 탈퇴 회원은 false 반환</p>
	 * <p>검증 방식: 반환값 확인</p>
	 */
	@Test
	@DisplayName("[정상] Member nickname으로 member 존재 확인, 탈퇴 회원 포함 검증- existsByNickname")
	void existsByNickname() {

		// given
		String memberNickname = member1.get().getNickname();
		String withdrawnMemberNickname = withdrawnMember1.get().getNickname();

		// when
		boolean memberExists = memberRepository.existsByNickname(memberNickname);
		boolean withdrawnMemberExists = memberRepository.existsByNickname(withdrawnMemberNickname);

		// then
		assertThat(memberExists).isTrue();
		assertThat(withdrawnMemberExists).isTrue();

	}

	/**
	 * <h2>[정상] 활성 Member id로 member 찾기 - findByIdAndActiveTrue</h2>
	 * <p>결과: 활성 회원은 Member 반환, 탈퇴 회원은 empty 반환</p>
	 * <p>검증 방식: 반환값 확인</p>
	 */
	@Test
	@DisplayName("[정상] 활성 Member id로 member 찾기 - findByIdAndActiveTrue")
	void findByIdAndActive() {

		// given

		// when
		Optional<Member> activeM = memberRepository.findByIdAndActiveTrue(1L);

		// then
		assertThat(activeM).isNotNull();
		assertThat(activeM.get().isActive()).isTrue();
		assertThat(activeM).hasSameHashCodeAs(member1);

	}

	@Test
	@DisplayName("[정상] 탈퇴한 Member id로 member 찾기 - findByIdAndActiveTrue")
	void findByIdAndActiveEmpty() {

		// given

		// when
		Optional<Member> inActiveM = memberRepository.findByIdAndActiveTrue(5L);

		// then
		assertThat(inActiveM).isEmpty();

	}

	/**
	 * <h2>[정상] 활성 Member id로 Member 찾기, 시점 별 구분 - existsByIdAndActiveTrueAndCreatedAtAfter</h2>
	 * <p>- second로 하는 경우 생성 시점 차이가 생겨 테스트 실패해 minutes으로 진행</p>
	 * <p>결과: 방금 생성만 true, 1, 2일 전은 false</p>
	 * <p>검증 방식: 반환값 확인</p>
	 */
	@Test
	@DisplayName("[정상] 활성 Member id로 Member 찾기, 시점 별 구분 - existsByIdAndActiveTrueAndCreatedAtAfter")
	void existsByIdAndActiveTrueAndCreatedAtAfterActive() {

		// given
		LocalDateTime now = LocalDateTime.now();

		// when
		// 1분 전 생성
		boolean activeNowMember = memberRepository.existsByIdAndActiveTrueAndCreatedAtGreaterThanEqual(1L,
			now.minusDays(1));

		// 1일 전 생성 - 근소한 시간차 발생
		boolean active1agoMember = memberRepository.existsByIdAndActiveTrueAndCreatedAtGreaterThanEqual(2L,
			now.minusDays(1));

		// 1일 전 생성 후 1분 유예 (시간차 해소)
		boolean active1agoPlus3MinMember = memberRepository.existsByIdAndActiveTrueAndCreatedAtGreaterThanEqual(2L,
			now.minusDays(1).minusMinutes(1));

		// 2일 전 생성
		boolean active2agoMember = memberRepository.existsByIdAndActiveTrueAndCreatedAtGreaterThanEqual(3L,
			now.minusDays(1));

		// then
		assertThat(activeNowMember).isTrue();
		assertThat(active1agoMember).isFalse();
		assertThat(active1agoPlus3MinMember).isTrue();
		assertThat(active2agoMember).isFalse();

	}

	/**
	 * <h2>[정상] 탈퇴 Member id로 Member 찾기, 시점 별 구분 - existsByIdAndActiveTrueAndCreatedAtAfter</h2>
	 * <p>결과: 전부 false</p>
	 * <p>검증 방식: 반환값 확인</p>
	 */
	@Test
	@DisplayName("[성공] 탈퇴 Member id로 Member 찾기, 시점 별 구분 - existsByIdAndActiveTrueAndCreatedAtAfter")
	void existsByIdAndActiveTrueAndCreatedAtAfterExpired() {

		// given
		LocalDateTime now = LocalDateTime.now();

		// when
		boolean withdrawnNowMember = memberRepository.existsByIdAndActiveTrueAndCreatedAtGreaterThanEqual(5L,
			now.minusMinutes(5));
		boolean withdrawn1agoMember = memberRepository.existsByIdAndActiveTrueAndCreatedAtGreaterThanEqual(7L,
			now.minusMinutes(5));
		boolean withdrawn2agoMember = memberRepository.existsByIdAndActiveTrueAndCreatedAtGreaterThanEqual(6L,
			now.minusMinutes(5));

		// then
		assertThat(withdrawnNowMember).isFalse();
		assertThat(withdrawn1agoMember).isFalse();
		assertThat(withdrawn2agoMember).isFalse();

	}

	/**
	 * <h2>[정상] 존재하지 않는 Member id로 Member 찾기, 시점 별 구분 - existsByIdAndActiveTrueAndCreatedAtAfter</h2>
	 * <p>결과: false</p>
	 * <p>검증 방식: 반환값 확인</p>
	 */
	@Test
	@DisplayName("[성공] 존재하지 않는 Member id로 Member 찾기, 시점 별 구분 - existsByIdAndActiveTrueAndCreatedAtAfter")
	void existsByIdAndActiveTrueAndCreatedAtAfterNonExist() {

		// given
		LocalDateTime now = LocalDateTime.now();

		// when
		boolean nonExistNowMember = memberRepository.existsByIdAndActiveTrueAndCreatedAtGreaterThanEqual(100L,
			now.minusMinutes(5));
		// then
		assertThat(nonExistNowMember).isFalse();

	}

	@Test
	@DisplayName("[확인] null을 입력받는 경우 - findByNicknameAndActiveTrue")
	void findByNicknameAndActiveTrueNull() {

		// when
		Optional<Member> member = memberRepository.findByNicknameAndActiveTrue(null);

		// then
		assertThat(member).isEmpty();

	}

}