package goorm.eagle7.stelligence.common.util;

import java.util.Random;

public class RandomUtils {

	private static final Random random = new Random();

	private RandomUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * 닉네임 중복이면 랜덤 닉네임 생성
	 * @param nickname 닉네임
	 * @return 닉네임+랜덤숫자 5자리
	 */
	public static String createNicknameWithRandomNumber(String nickname) {

		// nickname 중복 확인 후 중복이면 랜덤 생성
		int randomNumber = 10000 + random.nextInt(90000); // 10000 ~ 99999 범위의 숫자 생성
		return nickname + randomNumber;

	}

}
