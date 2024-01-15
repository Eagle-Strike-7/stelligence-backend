package goorm.eagle7.stelligence.config;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DummyInsertManager implements ApplicationRunner {

	private final DataSource dataSource;

	@Override
	public void run(ApplicationArguments args) {

		try (Connection conn = dataSource.getConnection()) {

			ScriptUtils.executeSqlScript(conn, new ClassPathResource("/dummy.sql"));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
