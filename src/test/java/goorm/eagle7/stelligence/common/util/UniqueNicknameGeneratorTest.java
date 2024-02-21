package goorm.eagle7.stelligence.common.util;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.domain.member.MemberRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
class UniqueNicknameGeneratorTest {

	@Mock
	private MemberRepository memberRepository;

	/**
	 * <h2>[성공] 중복 X 닉네임 제공, 중복되지 않는 닉네임 생성 성공</h2>
	 * <p>결과: 중복이 아닌 경우, 입력한 닉네임 반환</p>
	 * <p>검증 방식: 동등성 확인</p>
	 */
	@Test
	@DisplayName("[성공] 중복X 닉네임인 경우, 중복되지 않는 닉네임 생성 - generateUniqueNickname")
	void generateUniqueNicknameUnique() {

		// Given
		String baseNickname = "은하";
		when(memberRepository.existsByNickname(baseNickname)).thenReturn(false);
		String uniqueNickname = baseNickname;
		// When
		uniqueNickname = UniqueNicknameGenerator.generateUniqueNickname(uniqueNickname,
			nickname -> memberRepository.existsByNickname(nickname)
		);

		// Then
		assertThat(uniqueNickname).isEqualTo(baseNickname);
	}

	/**
	 * <h2>[정상] 중복 닉네임 제공, 랜덤 닉네임 생성 성공</h2>
	 * <p>결과: 중복인 경우, 랜덤 숫자가 추가된 새로운 닉네임 생성</p>
	 * <p>검증 방식: 닉네임 길이, baseNickname으로 시작하는지, 중복만큼 호출됐는지 확인</p>
	 * <p>- 중복이 아닐 때까지 닉네임에 랜덤 숫자를 변경해 새로운 닉네임 생성.</p>
	 */
	@Test
	@DisplayName("[정상] 중복 닉네임이고, 2자인 경우, 랜덤 숫자가 추가된 새로운 닉네임 생성(9 중복) - generateUniqueNickname")
	void generateUniqueNicknameWithRandomNumber() {

		// given
		String baseNickname = "은하";
		// 중복 9회, 중복이 아닌 경우
		when(memberRepository.existsByNickname(anyString())).thenReturn(true, true, true, true, true, true,
			true, true, true, false);

		// when
		String uniqueNickname = baseNickname;
		uniqueNickname = UniqueNicknameGenerator.generateUniqueNickname(uniqueNickname,
			nickname -> memberRepository.existsByNickname(nickname));

		// then
		assertThat(uniqueNickname)
			.isNotEqualTo(baseNickname)
			.startsWith(baseNickname)
			.isGreaterThan(baseNickname)
			.hasSize(baseNickname.length() + 5);

		verify(memberRepository, times(10)).existsByNickname(anyString());

		log.info("baseNickname = {}", baseNickname);
		log.info("uniqueNickname = {}", uniqueNickname);

	}

	/**
	 * <h2>[예외] 기본 닉네임이 없는 경우, 기본 닉네임으로 설정</h2>
	 * <p>결과: 기본 닉네임이 없는 경우, 기본 닉네임으로 설정</p>
	 * <p>검증 방식: 동등성 확인</p>
	 */
	@Test
	@DisplayName("[예외] 기본 닉네임이 없는 경우, 기본 닉네임으로 설정 - generateUniqueNickname")
	void generateUniqueNicknameWithNullBaseNickname() {

		// given
		String baseNickname = null;
		when(memberRepository.existsByNickname(any())).thenReturn(false);

		// when
		String uniqueNickname = UniqueNicknameGenerator.generateUniqueNickname(baseNickname,
			nickname -> memberRepository.existsByNickname(nickname)
		);
		// then
		assertThat(uniqueNickname).isEqualTo("은하");
	}

	/**
	 * <h2>[예외] 처음부터 15자인 경우, 중복이 아니면 그대로 반환</h2>
	 * <p>결과: 처음부터 15자인 경우, 중복이 아니면 그대로 반환</p>
	 * <p>검증 방식: 동등성 확인 및 호출 횟수</p>
	 */
	@Test
	@DisplayName("[예외] 처음 닉네임부터 15자인 경우, 중복이 아니면 그대로 반환 - generateUniqueNickname")
	void generateUniqueNicknameWithMaxLength() {

		// given
		String korBaseNickname = "일이삼사오육칠팔구십일이삼사오";
		when(memberRepository.existsByNickname(korBaseNickname)).thenReturn(false);

		// when
		String uniqueNicknameKor = UniqueNicknameGenerator.generateUniqueNickname(korBaseNickname,
			nickname -> memberRepository.existsByNickname(nickname));

		// then
		assertThat(uniqueNicknameKor).isEqualTo(korBaseNickname);
		verify(memberRepository, times(1) ).existsByNickname(korBaseNickname);

	}

	/**
	 * <h2>[예외] 10이상 15자 미만인 경우, 15자에서 남은 수만큼 랜덤으로 생성</h2>
	 * <p>결과: 10이상 15자 미만인 경우, 15자에서 남은 수만큼 랜덤으로 생성</p>
	 * <p>검증 방식: 길이, 포함하는 문자, 중복만큼 호출됐는지 확인</p>
	 */
	@Test
	@DisplayName("[예외] 10이상 15자 미만인 경우, 15자에서 남은 수만큼 랜덤으로 생성 - generateUniqueNickname")
	void generateUniqueNicknameWithOverMaxLengthAndDuplicate() {

		// given
		String korBaseNickname = "일이삼사오육칠팔구십일이삼사";
		when(memberRepository.existsByNickname(any())).thenReturn(true, false);

		// when
		String uniqueNicknameKor = UniqueNicknameGenerator.generateUniqueNickname(korBaseNickname,
			nickname -> memberRepository.existsByNickname(nickname));

		// then
		assertThat(uniqueNicknameKor)
			.isNotEqualTo(korBaseNickname)
			.startsWith("일이삼사오육칠팔구십일이삼사")
			.hasSize(15);
		verify(memberRepository, times(2)).existsByNickname(anyString());

	}

	/**
	 * <h2>[예외] 처음부터 15자, 중복인 경우</h2>
	 * <p>결과: 10글자만 남기고, 뒤는 랜덤으로 생성</p>
	 * <p>검증 방식: 길이, 포함하는 문자, 중복만큼 호출됐는지 확인</p>
	 */
	@Test
	@DisplayName("[예외] 처음부터 15자, 10글자만 보존, 15자로 생성- generateUniqueNickname")
	void generateUniqueNicknameWithMaxLengthAndDuplicate() {

		// given
		String korBaseNickname = "일이삼사오육칠팔구십일이삼사오";
		when(memberRepository.existsByNickname(anyString())).thenReturn(true, true, false);

		// when
		String uniqueNicknameKor = UniqueNicknameGenerator.generateUniqueNickname(korBaseNickname,
			nickname -> memberRepository.existsByNickname(nickname));

		// then
		assertThat(uniqueNicknameKor)
			.isNotEqualTo(korBaseNickname)
			.startsWith("일이삼사오육칠팔구십")
			.doesNotContain("일이삼사오육칠팔구십일")
			.hasSize(15);
		verify(memberRepository, times(3)).existsByNickname(anyString());

	}

}