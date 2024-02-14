package goorm.eagle7.stelligence.common.util;

import java.util.UUID;
import java.util.function.Predicate;

import org.springframework.util.StringUtils;

import goorm.eagle7.stelligence.api.exception.BaseException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class UniqueNicknameGenerator {

	private UniqueNicknameGenerator() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * <h2>유일한 닉네임 생성</h2>
	 * <p>- 중복이 아닐 때까지 닉네임에 랜덤 숫자를 추가해 새로운 닉네임 생성.</p>
	 * <p>- 최대 시도 횟수는 10회로 제한.</p>
	 * <p>- Predicate 인터페이스를 사용해 닉네임의 중복 여부를 외부에서 결정할 수 있도록 함.</p>
	 * @param baseNickname 기본 닉네임
	 * @param isDuplicate 중복 검사를 수행하는 함수
	 * @return 중복되지 않는 닉네임(랜덤숫자 5자리)
	 */ // TODO 파라미터는 변하는 게 좋지 않음!
	public static String generateUniqueNickname(String baseNickname, Predicate<String> isDuplicate) {

		// 기본 닉네임이 없으면 "은하"로 설정
		baseNickname = StringUtils.hasText(baseNickname) ? baseNickname : "은하";
		String nickname = baseNickname;
		// UUID를 사용하면 
		String uuid = UUID.randomUUID().toString();

		int attempt = 0;
		while (isDuplicate.test(nickname) && attempt < 10) {

			nickname = baseNickname + uuid.substring(0, 5 + attempt++);
			log.trace(" 닉네임 중복으로 새로 생성한 닉네임: {}", nickname);

		}
		if (attempt == 10) {
			throw new BaseException("닉네임 생성에 실패했습니다. 최대 시도 횟수를 초과했습니다.");
		}
		return nickname;
	}

}
