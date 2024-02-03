package goorm.eagle7.stelligence.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();

		// 특정 IP, 도메인에 허용
		// 특정 출처만 허용. 보안을 위해 구체적인 도메인 명시. - 개발 시 localhost로 설정
		config.setAllowedOrigins(java.util.List.of("**", "*"));

		// 특정 헤더만 허용. 클라이언트가 서버로 전송할 수 있는 HTTP 헤더를 제한함. "Authorization" 등
		// config.setAllowedHeaders(java.util.List.of("Cache-Control", "Content-Type", "Cookie", "Set-Cookie"));
		config.setAllowedHeaders(java.util.List.of("Cache-Control", "Content-Type", "Cookie", "Set-Cookie", "Authorization", "custom-header"));

		// 쿠키와 같은 인증 정보를 포함할 지 여부. 대부분의 경우 false로 설정해야 함.
		config.setAllowCredentials(true);

		// 특정 HTTP 메서드만 허용. 필요한 메서드만 명시적으로 허용하여 보안을 강화함.
		config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE"));

		// 브라우저가 CORS 설정을 캐시할 시간을 지정. 너무 짧지 않게 설정해 성능 개선.
		config.setMaxAge(3600L); // 예: 1시간

		source.registerCorsConfiguration("/api/**", config);
		return new CorsFilter(source);
	}

}
