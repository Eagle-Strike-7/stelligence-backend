package goorm.eagle7.stelligence.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import lombok.RequiredArgsConstructor;

/**
 * <h2>CORS 설정</h2>
 */
@Configuration
@RequiredArgsConstructor
public class CorsConfig {

	private final CorsProperties corsProperties;

	@Bean
	public CorsFilter corsFilter() {

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();

		// 허용할 IP, 도메인, 포트
		config.setAllowedOrigins(corsProperties.getAllowedOrigins());
		// 허용할 Http 메서드
		config.setAllowedMethods(corsProperties.getAllowedMethods());
		// 허용할 헤더
		config.setAllowedHeaders(corsProperties.getAllowedHeaders());
		// 쿠키와 같은 인증 정보를 포함할지 여부
		config.setAllowCredentials(corsProperties.getAllowedCredentials());
		// 브라우저가 CORS 설정을 캐시할 시간
		config.setMaxAge(corsProperties.getMaxAge());

		// URL별로 CORS 설정을 적용할 수 있도록 함.
		source.registerCorsConfiguration(corsProperties.getRegisterCorsConfiguration(), config);

		return new CorsFilter(source);

	}

}
