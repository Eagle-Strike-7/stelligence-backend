package goorm.eagle7.stelligence.domain.member.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RoleTest {

	/* fromValueDefaultUser - 기본값이 User라서 admin으로 test */

	/**
	 * <h2>[성공] 존재하는 value로 Role 생성</h2>
	 * <p>검증 방식: 실제 메서드 호출과 예상한 값이 같은지 확인</p>
	 * <p>결과: 원하는 value로 Role 생성 </p>
	 */
	@Test
	@DisplayName("[성공] 존재하는 value로 Role 생성 - fromValueDefaultUser")
	void fromValueDefaultUserSuccess() {

		// given
		String value = "ROLE_ADMIN";
		Role expected = Role.ADMIN;

		// when
		Role actual = Role.fromValueDefaultUser(value);

		// then
		assertThat(actual).isEqualTo(expected);

	}

	/**
	 * <h2>[예외] 존재하지 않는 value로 Role 생성 시 default</h2>
	 * <p>검증 방식: 예외 발생 입력인 경우, 기본값 출력되는지 확인</p>
	 * <p>결과: default로 USER 생성</p>
	 */
	@Test
	@DisplayName("[예외] 존재하지 않는 value로 Role 생성 시 default - fromValueDefaultUser")
	void fromValueDefaultUserFailNoValue() {

		// given
		String invalidValue = "notExist";
		Role expected = Role.USER;

		// when
		Role actual = Role.fromValueDefaultUser(invalidValue);

		// then
		assertThat(actual).isEqualTo(expected);

	}

	/**
	 * <h2>[예외] null, empty, blank(value)로 Role 생성 시 default</h2>
	 * <p>검증 방식: 예외 발생 입력인 경우, 기본값 출력되는지 확인</p>
	 * <p>결과: default로 USER 생성</p>
	 */
	@Test
	@DisplayName("[예외] null, empty, blank(value)로 Role 생성 시 default - fromValueDefaultUser")
	void fromValueDefaultUserNullValue() {

		// given
		Role expected = Role.USER;

		// when
		Role actual = Role.fromValueDefaultUser(null);
		Role actual2 = Role.fromValueDefaultUser("");
		Role actual3 = Role.fromValueDefaultUser(" ");

		// then
		assertThat(actual).isEqualTo(expected);
		assertThat(actual2).isEqualTo(expected);
		assertThat(actual3).isEqualTo(expected);

	}


	/* fromValue */

	/**
	 * <h2>[성공] 존재하는 value로 Role 생성</h2>
	 * <p>검증 방식: 실제 메서드 호출과 예상한 값이 같은지 확인</p>
	 * <p>결과: 원하는 value로 Role 생성 </p>
	 */
	@Test
	@DisplayName("[성공] 존재하는 value로 생성 시 성공 - fromValue")
	void fromValueSuccess() {

		// given
		String value = "ROLE_ADMIN";
		Role expected = Role.ADMIN;

		// when
		Role actual = Role.fromValue(value);

		// then
		assertThat(actual).isEqualTo(expected);

	}

	/**
	 * <h2>[예외] 존재하지 않는 value로 생성 시 IllegalArguEx</h2>
	 * <p>검증 방식: 예외 발생 입력인 경우, 예외 발생하는지 확인</p>
	 * <p>결과: IllegalArguEx 발생</p>
	 */
	@Test
	@DisplayName("[예외] 존재하지 않는 value로 생성 시 IllegalArguEx - fromValue")
	void fromValueFail() {

		// given
		String invalidValue = "notExist";

		// when, then
		assertThatThrownBy(
			() -> Role.fromValue(invalidValue))
			.isInstanceOf(IllegalArgumentException.class);

	}

	/**
	 * <h2>[예외] null, empty, blank(value)로 생성 시 IllegalArguEx</h2>
	 * <p>검증 방식: 예외 발생 입력인 경우, 예외 발생하는지 확인</p>
	 * <p>결과: IllegalArguEx 발생</p>
	 */
	@Test
	@DisplayName("[예외] null, empty, blank(value)로 생성 시 IllegalArguEx - fromValue")
	void fromValueNull() {

		// given - null, when, then
		assertThatThrownBy(
			() -> Role.fromValue(null))
			.isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(
			() -> Role.fromValue(""))
			.isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(
			() -> Role.fromValue(" "))
			.isInstanceOf(IllegalArgumentException.class);

	}


	/* fromValue */

	/**
	 * <h2>[성공] 존재하는 value로 Role 생성</h2>
	 * <p>검증 방식: 실제 메서드 호출과 예상한 값이 같은지 확인</p>
	 * <p>결과: 원하는 value로 Role 생성 </p>
	 */
	@Test
	@DisplayName("[성공] 존재하는 name으로 Role 생성 - valueOf")
	void valueOfSuccess() {

		// given
		String name = "ADMIN";
		Role expected = Role.ADMIN;

		// when
		Role actual = Role.valueOf(name);

		// then
		assertThat(actual).isEqualTo(expected);

	}

	/**
	 * <h2>[예외] null, empty, blank(name)로 Role 생성 시 NP/IllegalArguEx</h2>
	 * <p>검증 방식: 예외 발생 입력인 경우, 예외 발생하는지 확인</p>
	 * <p>결과: NP/IllegalArguEx 발생</p>
	 */
	@Test
	@DisplayName("[예외] null, empty, blank(name)로 Role 생성 시 NP/IllegalArguEx - valueOf")
	void valueOfFailNullname() {

		// given - null, when, then
		assertThatThrownBy(
			() -> Role.valueOf(null))
			.isInstanceOf(NullPointerException.class);

		// given - empty, blank, when, then
		assertThatThrownBy(
			() -> Role.valueOf(""))
			.isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(
			() -> Role.valueOf(" "))
			.isInstanceOf(IllegalArgumentException.class);

	}

	/**
	 * <h2>[예외] 존재하지 않는 name으로 Role 생성 시 IllegalArguEx</h2>
	 * <p>검증 방식: 예외 발생 입력인 경우, 예외 발생하는지 확인</p>
	 * <p>결과: IllegalArguEx 발생</p>
	 */
	@Test
	@DisplayName("[예외] 존재하지 않는 name으로 Role 생성 시 IllegalArguEx - valueOf")
	void valueOfFailNoName() {

		// given
		String name = "notExist";

		// when, then
		assertThatThrownBy(
			() -> Role.valueOf(name))
			.isInstanceOf(IllegalArgumentException.class);

	}

}