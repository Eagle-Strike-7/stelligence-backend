package goorm.eagle7.stelligence.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@PropertySource("classpath:application-cors.yml")
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {

	private List<String> allowedOrigins;
	private List<String> allowedMethods;
	private List<String> allowedHeaders;
	private List<String> exposedHeaders;
	private Boolean allowedCredentials;
	private Long maxAge;
	private String registerCorsConfiguration;

}
