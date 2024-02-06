package goorm.eagle7.stelligence.domain.member;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
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
	private Optional<Member> member2;
	private Optional<Member> member3;
	private Optional<Member> member4;
	private Optional<Member> withdrawnMember1;
	private Optional<Member> withdrawnMember2;
	private Optional<Member> withdrawnMember3;

	@BeforeEach
	void setUp() {

		// 1~4 line: 회원
		member1 = memberRepository.findById(1L);
		member2 = memberRepository.findById(2L);
		member3 = memberRepository.findById(3L);
		member4 = memberRepository.findById(4L);

		// 5~7 line: 탈퇴한 회원
		withdrawnMember1 = memberRepository.findById(5L);
		withdrawnMember2 = memberRepository.findById(6L);
		withdrawnMember3 = memberRepository.findById(7L);

	}

	@Test
	void findByNickname() {
	}

	@Test
	void existsByNickname() {
	}

	@Test
	void findByIdAndActiveTrue() {

		// given

		// when
		Optional<Member> activeM = memberRepository.findByIdAndActiveTrue(1L);
		Optional<Member> inActiveM = memberRepository.findByIdAndActiveTrue(4L);

		// then
		assertThat(activeM).isNotNull();
		assertThat(activeM.get().isActive()).isTrue();
		assertThat(activeM).hasSameHashCodeAs(member1);
		assertThat(inActiveM).isEmpty();

	}
}