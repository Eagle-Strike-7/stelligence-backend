package goorm.eagle7.stelligence.config.mockdata;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import goorm.eagle7.stelligence.config.DummyInsertManager;

@TestConfiguration
public class MockDataConfig {

	@Autowired
	DataSource dataSource;

	@Bean
	public DummyInsertManager dummyInsertManager() {
		return new DummyInsertManager(dataSource);
	}
}
