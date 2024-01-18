package goorm.eagle7.stelligence.common.auth.memberinfo;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.annotation.Nullable;

/**
 *  Auth 애노테이션을 사용하면, MemberInfo를 받을 수 있다.
 */
@Component
public class MemberInfoArgumentResolver
	implements HandlerMethodArgumentResolver {


	/**
	 * supportsParameter
	 * @param parameter @Auth 어노테이션인지 확인
	 * @return 맞다면 true, 아니면 false
	 *
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(Auth.class);
	}

	/**
	 * resolveArgument
	 * param 중 사용 하는 것 없음.
	 * 바로 MemberContextHolder(ThreadLocal)에서 MemberInfo를 가져와 반환한다.
	 * @return MemberInfo(@Auth 어노테이션을 사용하면 MemberInfo를 받을 수 있다.)
	 *
	 */
	@Override
	public Object resolveArgument(
		@Nullable MethodParameter parameter,
		ModelAndViewContainer mavContainer,
		@Nullable NativeWebRequest webRequest,
		WebDataBinderFactory binderFactory) {
		return MemberInfoContextHolder.getMemberInfo();
	}
}
