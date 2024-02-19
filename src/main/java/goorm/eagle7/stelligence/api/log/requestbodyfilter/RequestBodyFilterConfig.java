package goorm.eagle7.stelligence.api.log.requestbodyfilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RequestBodyFilterConfig는 RequestBodyFormatter에 대한 설정을 정의합니다.
 */
@Configuration
class RequestBodyFilterConfig {

	@Bean
	public RequestBodyFormatter requestBodyFormatter() {
		return new RequestBodyFullContentFormatter();
	}
}
