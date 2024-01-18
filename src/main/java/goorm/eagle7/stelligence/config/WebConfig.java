package goorm.eagle7.stelligence.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfoArgumentResolver;
import lombok.RequiredArgsConstructor;

/**
 * 1. memberInfoArgumentResolver를 WebMvcConfigurer에 등록해 HandlerMethodArgumentResolver로 사용하게 한다.
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
	private final MemberInfoArgumentResolver memberInfoArgumentResolver;

	/**
	 * memberInfoArgumentResolver를 WebMvcConfigurer에 등록해 HandlerMethodArgumentResolver로 사용하게 한다. -> 안 하면 Filter 접속하고 바로 Jwt 검증 실패
	 * @param resolvers initially an empty list
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(memberInfoArgumentResolver);
	}

}