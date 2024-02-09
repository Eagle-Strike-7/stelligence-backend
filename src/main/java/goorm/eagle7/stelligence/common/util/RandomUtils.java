package goorm.eagle7.stelligence.common.util;

import java.util.Random;
import java.util.function.BooleanSupplier;

import org.springframework.util.StringUtils;

import goorm.eagle7.stelligence.api.exception.BaseException;

public class RandomUtils {

	private static final Random random = new Random();

	private RandomUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * <h2>유일한 닉네임 생성</h2>
	 * <p>- 중복이 아닐 때까지 닉네임에 랜덤 숫자를 추가해 새로운 닉네임 생성.</p>
	 * <p>- 최대 시도 횟수는 10회로 제한.</p>
	 * <p>- BooleanSupplier 인터페이스를 사용해 닉네임의 중복 여부를 외부에서 결정할 수 있도록 함.</p>
	 * @param baseNickname 기본 닉네임
	 * @param isDuplicate 중복 검사를 수행하는 함수
	 * @return 중복되지 않는 닉네임(랜덤숫자 5자리)
	 */
	public static String generateUniqueNickname(String baseNickname, BooleanSupplier isDuplicate) {

		String nickname = StringUtils.hasText(baseNickname) ? baseNickname : "은하";
		int attempt = 0;

		while (isDuplicate.getAsBoolean() && attempt < 10) {
			int randomNumber = 10000 + random.nextInt(90000); // 10000 ~ 99999 범위의 숫자 생성
			nickname = baseNickname + randomNumber;
			attempt++;
		}
		if (attempt == 10) {
			throw new BaseException("닉네임 생성에 실패했습니다. 최대 시도 횟수를 초과했습니다.");
		}
		return nickname;
	}

}