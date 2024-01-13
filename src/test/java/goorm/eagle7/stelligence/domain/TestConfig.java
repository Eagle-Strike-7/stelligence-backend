package goorm.eagle7.stelligence.domain;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import goorm.eagle7.stelligence.common.sequence.SectionIdGenerator;

@TestConfiguration
public class TestConfig {

	@Bean
	public SectionIdGenerator sectionIdGenerator() {
		return new MockSectionIdGenerator();
	}
}
