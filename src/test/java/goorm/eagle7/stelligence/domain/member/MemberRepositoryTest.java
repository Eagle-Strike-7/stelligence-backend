package goorm.eagle7.stelligence.domain.member;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import goorm.eagle7.stelligence.config.mockdata.WithMockData;
import goorm.eagle7.stelligence.domain.member.model.Member;

@DataJpaTest
@WithMockData
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
		boolean memberExists = memberRepository.existsByNicknameAndActiveTrue(memberNickname);
		boolean withdrawnMemberExists = memberRepository.existsByNicknameAndActiveTrue(withdrawnMemberNickname);

		// then
		assertThat(memberExists).isTrue();
		assertThat(withdrawnMemberExists).isFalse();

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

}