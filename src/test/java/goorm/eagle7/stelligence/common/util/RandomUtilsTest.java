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

	@Test
	@DisplayName("[정상] 중복X 닉네임인 경우, 중복되지 않는 닉네임 생성 - existsByNicknameAndActiveTrue")
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

	@Test
	@DisplayName("[예외] 중복 닉네임인 경우, 중복되지 않는 닉네임 생성 실패 - existsByNicknameAndActiveTrue")
	void generateUniqueNicknameDuplicateFail() {

		// Given
		String baseNickname = "은하";
		when(memberRepository.existsByNicknameAndActiveTrue(baseNickname)).thenReturn(true);

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