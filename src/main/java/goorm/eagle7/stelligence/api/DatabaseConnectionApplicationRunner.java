package goorm.eagle7.stelligence.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseConnectionApplicationRunner implements ApplicationRunner {

	private final DataSource dataSource;

	private final RedisTemplate<String, String> redisTemplate;

	private final Neo4jClient neo4jClient;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(
			"SELECT 1")) {

			if (rs.next()) {
				log.info("MySQL connection test successful.");
			} else {
				log.error("MySQL connection test failed.");
			}
		} catch (Exception e) {
			log.error("Error testing MySQL connection: " + e.getMessage());
		}

		try {
			String key = "testKey";
			String value = "testValue";

			redisTemplate.opsForValue().set(key, value);
			String redisValue = redisTemplate.opsForValue().get(key);

			if (value.equals(redisValue)) {
				log.info("Redis connection test successful.");
			} else {
				log.error("Redis connection test failed.");
			}
		} catch (Exception e) {
			log.error("Error testing Redis connection: " + e.getMessage());
		}

		//test for neo4j
		try {
			neo4jClient.query("RETURN 1").fetch().one();
			log.info("Neo4j connection test successful.");
		} catch (Exception e) {
			log.error("Error testing Neo4j connection: " + e.getMessage());
		}
	}

}
