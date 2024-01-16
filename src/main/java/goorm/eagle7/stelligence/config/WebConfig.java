package goorm.eagle7.stelligence.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfoArgumentResolver;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfoInterceptor;
import lombok.RequiredArgsConstructor;

/**
 * 1. memberInfoArgumentResolver를 WebMvcConfigurer에 등록해 HandlerMethodArgumentResolver로 사용하게 한다.
 * 2. memberInfoInterceptor를 WebMvcConfigurer에 등록해 Interceptor로 사용하게 한다.
 *
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
	private final MemberInfoArgumentResolver memberInfoArgumentResolver;
	private final MemberInfoInterceptor memberInfoInterceptor;

	/**
	 * memberInfoInterceptor를 WebMvcConfigurer에 등록해 Interceptor로 사용하게 한다. -> 안 하면 @Auth 애노테이션 사용 시 MemberInfo가 null이 된다.
	 * @param registry InterceptorRegistry
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		// TODO new MemberInfoInterceptor()로 생성하는 방식과 @Component로 생성하는 방식 중 어떤 것이 더 좋은가?
		registry.addInterceptor(memberInfoInterceptor);
	}

	/**
	 * memberInfoArgumentResolver를 WebMvcConfigurer에 등록해 HandlerMethodArgumentResolver로 사용하게 한다. -> 안 하면 Filter 접속하고 바로 Jwt 검증 실패 -> TODO why?
	 * @param resolvers initially an empty list
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(memberInfoArgumentResolver);
	}

}
