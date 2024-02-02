package goorm.eagle7.stelligence.common.auth.memberinfo;

import java.util.Collection;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import goorm.eagle7.stelligence.domain.member.model.Role;
import jakarta.annotation.Nullable;

/**
 *  Auth 애노테이션에서 사용할 객체 mapping (MemberInfo)
 *  MemberInfo: memberId, role을 가지고 있는 객체
 */
@Component
public class MemberInfoArgumentResolver
	implements HandlerMethodArgumentResolver {

	/**
	 * supportsParameter
	 * @param parameter
	 * @return @Auth 어노테이션이 맞다면 true, 아니면 false
	 *
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(Auth.class);
	}

	/**
	 * @Auth 어노테이션을 사용한 파라미터에 대해 MemberInfo 객체를 생성해 반환
	 * SecurityContextHolder(ThreadLocal)에서 User 객체를 가져와서 MemberInfo 객체 생성해 반환
	 * @return MemberInfo(@ Auth 사용 시MemberInfo 사용 가능)
	 */
	@Override
	public Object resolveArgument(
		@Nullable MethodParameter parameter,
		ModelAndViewContainer mavContainer,
		@Nullable NativeWebRequest webRequest,
		WebDataBinderFactory binderFactory) {

		User user = (User)SecurityContextHolder.getContext()
			.getAuthentication().getPrincipal();
		long memberId = Long.parseLong(user.getUsername());

		Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
		Role role = convertAuthoritiesToRole(authorities);

		return MemberInfo.of(memberId, role);
	}

	/**
	 * GrantedAuthority를 Role로 변환
	 * @param authorities GrantedAuthority
	 * @return Role
	 */
	private Role convertAuthoritiesToRole(Collection<? extends GrantedAuthority> authorities) {
		if (authorities != null && !authorities.isEmpty()) {
			String authorityName = authorities.iterator().next().getAuthority();
			// TODO valueOf 차이
			return Role.getRoleFromString(authorityName);
		}
		return Role.USER; // 기본값으로 설정
	}
}
