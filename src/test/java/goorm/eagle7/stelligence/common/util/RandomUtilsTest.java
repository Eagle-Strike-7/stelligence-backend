package goorm.eagle7.stelligence.common.util;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.member.MemberRepository;

@ExtendWith(MockitoExtension.class)
class RandomUtilsTest {

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
		when(memberRepository.existsByNicknameAndActiveTrue(baseNickname)).thenReturn(false);

		// When
		String uniqueNickname = RandomUtils.generateUniqueNickname(baseNickname,
			() -> memberRepository.existsByNicknameAndActiveTrue(baseNickname)
		);

		// Then
		assertThat(uniqueNickname).isEqualTo(baseNickname);
	}

	/**
	 * <h2>[정상] 중복 닉네임 제공, 랜덤 닉네임 생성 성공</h2>
	 * <p>결과: 중복인 경우, 랜덤 숫자가 추가된 새로운 닉네임 생성</p>
	 * <p>검증 방식: 닉네임 길이, baseNickname으로 시작하는지, 중복만큼 호출됐는지 확인</p>
	 * <p>- 중복이 아닐 때까지 닉네임에 랜덤 숫자를 추가해 새로운 닉네임 생성.</p>
	 */
	@Test
	@DisplayName("[정상] 중복 닉네임인 경우, 랜덤 숫자가 추가된 새로운 닉네임 생성(9 중복) - generateUniqueNickname")
	void generateUniqueNicknameWithRandomNumber() {

		// Given
		String baseNickname = "은하";

		// 처음 두 번의 호출에서는 true를 반환하여 중복을 시뮬레이션하고, 세 번째 호출에서 false를 반환하여 중복이 아님을 나타냄
		when(memberRepository.existsByNicknameAndActiveTrue(anyString())).thenReturn(true, true, true, true, true, true, true, true, true, false);

		// When
		String uniqueNickname = RandomUtils.generateUniqueNickname(baseNickname,
			() -> memberRepository.existsByNicknameAndActiveTrue(baseNickname)
		);

		// Then
		assertThat(uniqueNickname)
			.isNotEqualTo(baseNickname)
			.startsWith(baseNickname);
		assertThat(uniqueNickname.length()).isGreaterThan(baseNickname.length());
		verify(memberRepository, times(10)).existsByNicknameAndActiveTrue(baseNickname);

		System.out.println("baseNickname = " + baseNickname);
		System.out.println("uniqueNickname = " + uniqueNickname);

	}

	/**
	 * <h2>[예외] 중복 닉네임 생성 실패 - 10회</h2>
	 * <p>결과: 중복인 경우, BaseException 발생</p>
	 * <p>검증 방식: 예외 발생 여부</p>
	 * <p>- 최대 시도 횟수는 10회로 제한.</p>
	 */
	@Test
	@DisplayName("[예외] 중복 닉네임인 경우, 중복되지 않는 닉네임 생성 실패 - generateUniqueNickname")
	void generateUniqueNicknameDuplicateFail() {

		// Given
		String baseNickname = "은하";
		when(memberRepository.existsByNicknameAndActiveTrue(baseNickname)).thenReturn(true, true, true, true, true, true, true, true, true, true);

		// When

		// Then
		assertThatThrownBy(() -> {
			RandomUtils.generateUniqueNickname(baseNickname,
				() -> memberRepository.existsByNicknameAndActiveTrue(baseNickname)
			);
		}).isInstanceOf(BaseException.class)
			.hasMessageContaining("최대 시도 횟수를 초과했습니다.");

	}

}