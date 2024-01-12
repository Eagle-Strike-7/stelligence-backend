package goorm.eagle7.stelligence.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Jpa Auditing 기능을 활성화하기 위한 설정 클래스입니다.
 * main 클래스에 애노테이션을 직접 붙이지 않고 느슨하게 결합합니다.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
