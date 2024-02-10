package goorm.eagle7.stelligence.domain.member.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RoleTest {

	@Test
	@DisplayName("[성공] - 존재하는 value로 Role 생성 - fromValue")
	void fromValue() {

		// given
		String value = "ROLE_ADMIN";
		Role expected = Role.ADMIN;

		// when
		Role actual = Role.fromValue(value);

		// then
		assertThat(actual).isEqualTo(expected);

	}

	@Test
	@DisplayName("[실패] - 존재하지 않는 value로 Role 생성 - fromValue")
	void fromValueFail() {

		// given
		String value = "ROLE_GUEST";
		Role expected = Role.USER;

		// when
		Role actual = Role.fromValue(value);

		// then
		assertThat(actual).isEqualTo(expected);

	}

	@Test
	void fromValueNull() {

		// given
		String value = "ADMIN";
		Role expected = Role.USER;

		// when
		Role actual = Role.fromValue(value);

		// then
		assertThat(actual).isEqualTo(expected);

	}

	@Test
	void valueOfSuccess() {

		// given
		String name = "ADMIN";
		Role expected = Role.ADMIN;

		// when
		Role actual = Role.valueOf(name);

		// then
		assertThat(actual).isEqualTo(expected);

	}

	@Test
	void valueOfFail() {

		// given - null, when, then
		assertThatThrownBy(
			() -> Role.valueOf(null))
			.isInstanceOf(NullPointerException.class);

	}

	@Test
	void valueOfFail2() {

		// given
		String name = "GUEST";

		// when, then
		assertThatThrownBy(
			() -> Role.valueOf(name))
			.isInstanceOf(IllegalArgumentException.class);

	}

	@Test
	void valueOfFail3() {

		// given
		String name = "ROLE_ADMIN";

		// when, then
		assertThatThrownBy(
			() -> Role.valueOf(name))
			.isInstanceOf(IllegalArgumentException.class);

	}

}