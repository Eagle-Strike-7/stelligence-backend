package goorm.eagle7.stelligence.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;

/**
 * Swagger 설정 클래스입니다.
 * http://{host}/api-docs 로 접속하면 api 문서를 확인할 수 있습니다.
 */
@OpenAPIDefinition(
	servers = {
		@Server(url = "https://api.stelligence.site/", description = "Default Server URL")
	}
)
@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.info(new io.swagger.v3.oas.models.info.Info()
				.title("Stelligence API")
				.description("Stelligence API Documentation")
				.version("v1"));
	}
}
