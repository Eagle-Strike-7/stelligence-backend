package goorm.eagle7.stelligence.common.login;

import java.util.Random;

class RandomUtils {

	private static final Random random = new Random();

	private RandomUtils() {
		throw new IllegalStateException("Utility class");
	}

	// 5자리 랜덤 숫자를 추가하는 메서드
	public static String createNicknameWithRandomNumber(String nickname) {

		// nickname 중복 확인 후 중복이면 랜덤 생성
		int randomNumber = 10000 + random.nextInt(90000); // 10000 ~ 99999 범위의 숫자 생성

		return nickname + randomNumber;

	}

}
