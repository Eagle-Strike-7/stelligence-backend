package goorm.eagle7.stelligence.common.auth.memberinfo;

import static org.assertj.core.api.Assertions.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import goorm.eagle7.stelligence.domain.member.model.Role;

@ExtendWith(MockitoExtension.class)
class MemberInfoArgumentResolverTest {

	@InjectMocks
	private MemberInfoArgumentResolver resolver;

	@Test
	@DisplayName("[성공] securityContext의 정보를 @Auth에 저장 - resolveArgument")
	void resolveArgumentSuccess() throws NoSuchMethodException {

		// given
		// security context 설정
		User user = new User("1", "", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
		Authentication authentication = new TestingAuthenticationToken(user, null);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		MethodParameter parameter = new MethodParameter(
			TestController.class.getMethod("methodWithAuthAnnotation", String.class), 0);

		// when
		MemberInfo result = (MemberInfo)resolver.resolveArgument(parameter, null, null, null);

		// then
		assertThat(result.getId()).isEqualTo(1);
		assertThat(result.getRole()).isEqualTo(Role.USER);

	}

	@Test
	@DisplayName("[실패] 인증 불가 시 securityContext에 정보 저장 X, @Auth null - resolveArgument")
	void resolveArgumentFailure() throws NoSuchMethodException {

		// given
		// security context 설정
		User user = new User("anonymousUser", "", Collections.emptyList());
		Authentication authentication = new TestingAuthenticationToken(user, null);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		MethodParameter parameter = new MethodParameter(
			TestController.class.getMethod("methodWithAuthAnnotation", String.class), 0);

		// when
		MemberInfo memberInfo = (MemberInfo)resolver.resolveArgument(parameter, null, null, null);

		// then
		assertThat(memberInfo).isNull();

	}
	@Test
	@DisplayName("[확인] authorities 여러 개, 없는 role(admin 앞)이 있는 경우 동작 확인 - resolveArgument")
	void resolveArgumentWithMultipleAuthoritiesWithNoRole() throws NoSuchMethodException {

		// given
		// security context 설정
		List<SimpleGrantedAuthority> authorities = List.of(
			new SimpleGrantedAuthority("ROLE_USER"),
			new SimpleGrantedAuthority("ROLE_ADMIN"),
			new SimpleGrantedAuthority("ROLE_A")
		);

		User user = new User("1", "", authorities);
		Authentication authentication = new TestingAuthenticationToken(user, null);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		MethodParameter parameter = new MethodParameter(
			TestController.class.getMethod("methodWithAuthAnnotation", String.class), 0);

		// when
		MemberInfo result = (MemberInfo)resolver.resolveArgument(parameter, null, null, null);

		// then
		// ADMIN 기준 없는 ROLE이 앞이라 USER가 FindFirst 반환값
		assertThat(result.getId()).isEqualTo(1);
		assertThat(result.getRole())
			.isEqualTo(Role.USER);

	}

	/**
	 * <h2>authorities 여러 개인 경우 동작 확인</h2>
	 * <p>- authorities 내부적으로 Set으로 저장, 현재는 알파벳 순으로 list에서 찾는 순서 결정</p>
	 */
	@Test
	@DisplayName("[확인] authorities 여러 개, 없는 role(admin 뒤)이 있는 경우 동작 확인 - resolveArgument")
	void resolveArgumentWithMultipleAuthorities() throws NoSuchMethodException {

		// given
		// security context 설정
		List<SimpleGrantedAuthority> authorities = List.of(
			new SimpleGrantedAuthority("ROLE_MANAGER"),
			new SimpleGrantedAuthority("ROLE_USER"),
			new SimpleGrantedAuthority("ROLE_ADMIN")
		);

		User user = new User("1", "", authorities);
		Authentication authentication = new TestingAuthenticationToken(user, null);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		MethodParameter parameter = new MethodParameter(
			TestController.class.getMethod("methodWithAuthAnnotation", String.class), 0);

		// when
		MemberInfo result = (MemberInfo)resolver.resolveArgument(parameter, null, null, null);

		// then
		// ADMIN 기준 없는 ROLE이 나중이라 ADMIN이 FindFirst 반환값
		assertThat(result.getId()).isEqualTo(1);
		assertThat(result.getRole())
			.isEqualTo(Role.ADMIN);

	}

	@Test
	@DisplayName("[확인] authorities에 존재하는 role, 여러 개인 경우 동작 확인 - resolveArgument")
	void resolveArgumentWithMultipleAuthorities2() throws NoSuchMethodException {

		// given
		// security context 설정
		List<SimpleGrantedAuthority> authorities = List.of(
			new SimpleGrantedAuthority("ROLE_ADMIN"),
			new SimpleGrantedAuthority("ROLE_USER")
		);

		User user = new User("1", "", authorities);
		Authentication authentication = new TestingAuthenticationToken(user, null);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		MethodParameter parameter = new MethodParameter(
			TestController.class.getMethod("methodWithAuthAnnotation", String.class), 0);

		// when
		MemberInfo result = (MemberInfo)resolver.resolveArgument(parameter, null, null, null);

		// then - findFirst라서 LIST 순서가 아닌 role 정의 시 순서
		assertThat(result.getId()).isEqualTo(1);
		assertThat(result.getRole())
			.isEqualTo(Role.ADMIN);

	}

	@Test
	@DisplayName("[확인] 없는 Role인 경우 User 반환 - resolveArgument")
	void resolveArgumentWithNoRole() throws NoSuchMethodException {

		// given
		// security context 설정
		List<SimpleGrantedAuthority> authorities = List.of(
			new SimpleGrantedAuthority("ROLE_AAA"),
			new SimpleGrantedAuthority("ROLE_MANAGER")
		);

		User user = new User("1", "", authorities);
		Authentication authentication = new TestingAuthenticationToken(user, null);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		MethodParameter parameter = new MethodParameter(
			TestController.class.getMethod("methodWithAuthAnnotation", String.class), 0);

		// when
		MemberInfo result = (MemberInfo)resolver.resolveArgument(parameter, null, null, null);

		// then
		assertThat(result.getId()).isEqualTo(1);
		assertThat(result.getRole())
			.isEqualTo(Role.USER);

	}

	private class TestController {
		public void methodWithAuthAnnotation(@Auth String memberId) {
		}

		public void methodWithoutAuthAnnotation(String memberId) {
		}

	}
}