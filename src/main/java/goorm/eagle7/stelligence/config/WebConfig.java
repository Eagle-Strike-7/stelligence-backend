package goorm.eagle7.stelligence.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfoArgumentResolver;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
	private final MemberInfoArgumentResolver memberInfoArgumentResolver;

	/**
	 * <h2>memberInfoArgumentResolver 등록</h2>
	 * @param resolvers initially an empty list
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(memberInfoArgumentResolver);
	}

}
