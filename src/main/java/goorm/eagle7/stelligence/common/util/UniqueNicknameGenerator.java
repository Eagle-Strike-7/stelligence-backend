package goorm.eagle7.stelligence.common.util;

import java.util.UUID;
import java.util.function.Predicate;

import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class UniqueNicknameGenerator {

	private static final int MAX_UUID_LENGTH = 5;
	private static final int MAX_ATTEMPT = 10;
	private static final int MAX_NICKNAME_LENGTH = 15;

	private UniqueNicknameGenerator() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * <h2>유일한 닉네임 생성</h2>
	 * <p>- 중복이 아닐 때까지 닉네임에 랜덤 숫자를 추가해 새로운 닉네임 생성.</p>
	 * <p>- 최대 시도 횟수는 10회, 최대 15자.</p>
	 * <p>- Predicate 인터페이스를 사용해 닉네임의 중복 여부를 외부에서 결정할 수 있도록 함.</p>
	 * @param baseNickname 기본 닉네임
	 * @param isDuplicate 중복 검사를 수행하는 함수
	 * @return 중복되지 않는 닉네임(랜덤 문자는 최대 5자리)
	 */
	public static String generateUniqueNickname(String baseNickname, Predicate<String> isDuplicate) {

		// 기본 닉네임이 없으면 "은하"로 설정
		// 파라미터 보호 및 새로운 시도 시, baseNickname에 붙는 문자만 다르게 하기 위해 baseNickname과 nickname 분리
		baseNickname = StringUtils.hasText(baseNickname) ? baseNickname : "은하";

		String nickname = baseNickname;

		for (int attempt = 0; attempt < MAX_ATTEMPT; attempt++) {

			// 중복이 아니고, 15자 이내 닉네임이면 반환
			if (nickname.length() <= MAX_NICKNAME_LENGTH
				&& !isDuplicate.test(nickname)) {
				return nickname;
			}

			// 새로운 닉네임 창출
			nickname = generateNewNickname(baseNickname);

		}
		return nickname;
	}

	private static String generateNewNickname(String baseNickname) {

		// 15자 이상인 경우, 10자로 자르기
		if (baseNickname.length() >= MAX_NICKNAME_LENGTH) {
			baseNickname = baseNickname.substring(0, MAX_NICKNAME_LENGTH - MAX_UUID_LENGTH);
		}
		// 랜덤 문자 5자리 생성
		String uuid = UUID.randomUUID().toString().substring(0, MAX_UUID_LENGTH);

		// 닉네임 생성, 15자 초과 시, 15자로 자르기
		String newNickname = baseNickname + uuid;
		if (newNickname.length() > MAX_NICKNAME_LENGTH) {
			newNickname = newNickname.substring(0, MAX_NICKNAME_LENGTH);
		}

		log.trace(" 닉네임 중복으로 새로 생성한 닉네임: {}", newNickname);
		return newNickname;

	}

}


