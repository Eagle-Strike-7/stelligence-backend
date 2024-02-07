package goorm.eagle7.stelligence.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * <h2>CORS 설정</h2>
 * <p>CorsFilter: CORS 설정을 적용하는 필터</p>
 * <p>UrlBasedCorsConfigurationSource: URL별로 CORS 설정을 적용할 수 있도록 하는 클래스</p>
 * <p>allowedOrigins: 허용할 IP, 도메인</p>
 * <p>allowedMethods: 허용할 HTTP 메서드</p>
 * <p>allowedHeaders: 허용할 HTTP 헤더</p>
 * <p>allowCredentials: 쿠키와 같은 인증 정보를 포함할지 여부</p>
 * <p>maxAge: 브라우저가 CORS 설정을 캐시할 시간</p>
 */ // TODo yml 관리 고려
@Configuration
public class CorsConfig {

	@Bean
	public CorsFilter corsFilter() {

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();

		config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://www.stelligence.site", "https://api.stelligence.site", "http://localhost:8080"));
		config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(Arrays.asList("Set-Cookie", "Content-Type"));
		config.setAllowCredentials(true);
		config.setMaxAge(3600L);

		source.registerCorsConfiguration("/api/**", config);

		return new CorsFilter(source);
	}

}
