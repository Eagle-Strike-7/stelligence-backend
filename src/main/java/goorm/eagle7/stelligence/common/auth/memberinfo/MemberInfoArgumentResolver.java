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
 *  <h2>Auth 애노테이션에서 사용할 객체 mapping (MemberInfo)</h2>
 *  <p>- Auth 애노테이션을 사용한 파라미터에 대해 MemberInfo 객체를 생성해 반환</p>
 *  <p>- SecurityContextHolder(ThreadLocal)에서 User 객체를 가져와서 MemberInfo 객체 생성해 반환</p>
 *  <p>- MemberInfo: memberId, role을 가지고 있는 객체</p>
 */
@Component
public class MemberInfoArgumentResolver
	implements HandlerMethodArgumentResolver {

	/**
	 * <h2>Auth.class 확인</h2>
	 * @param parameter 파라미터
	 * @return @Auth 어노테이션이 맞다면 true, 아니면 false
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(Auth.class);
	}

	/**
	 * <h2>해당 객체 생성해 반환</h2>
	 * <p>- Auth 애노테이션을 사용한 파라미터에 대해 MemberInfo 객체 생성 및 반환</p>
	 * <p>- SecurityContextHolder(ThreadLocal)에서 User 객체 가져옴.</p>
	 * @return MemberInfo @Auth 사용 시MemberInfo 사용 가능
	 */
	@Override
	public Object resolveArgument(
		@Nullable MethodParameter parameter,
		ModelAndViewContainer mavContainer,
		@Nullable NativeWebRequest webRequest,
		WebDataBinderFactory binderFactory) {

		if(SecurityContextHolder.getContext()
			.getAuthentication().getPrincipal() instanceof User user){
			long memberId = Long.parseLong(user.getUsername());

			Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
			Role role = convertAuthoritiesToRole(authorities);

			return MemberInfo.of(memberId, role);

		}
		return null;

	}

	/**
	 * <h2>GrantedAuthority를 Role로 변환</h2>
	 * @param authorities GrantedAuthority
	 * @return Role 변환된 Role, 없으면 기본값으로 USER
	 */
	private Role convertAuthoritiesToRole(Collection<? extends GrantedAuthority> authorities) {
		return authorities.stream()
			.findFirst() // 첫 번째 GrantedAuthority 객체를 Optional로 반환
			.map(GrantedAuthority::getAuthority) // GrantedAuthority의 권한 이름을 가져옴
			.map(Role::fromValue) // 권한 이름을 Role로 변환
			.orElse(Role.USER); // Optional이 비어있는 경우 기본값으로 Role.USER 반환
	}

}
