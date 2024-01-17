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

		// neo4j의 fulltextindex를 체크하는 로직
		try {
			// `fulltext.analyzer`: 'cjk'는 중/일/한국어 특화 분석기
			// `fulltext.eventually_consistent`: false : 삽입에 대해 인덱싱을 지연하지 않고 즉시 적용
			String neo4jFulltextIndexQuery = "create fulltext index documentTitleIndex"
				+ " if not exists for (n:DocumentNode) on each [n.title]"
				+ " OPTIONS {"
				+ "  indexConfig: {"
				+ "    `fulltext.analyzer`: 'cjk', `fulltext.eventually_consistent`: false"
				+ "  }"
				+ " };";
			neo4jClient.query(neo4jFulltextIndexQuery).run();
			log.info("Neo4j fulltext index has checked successful.");
		} catch (Exception e) {
			log.error("Error checking Neo4j fulltext index: " + e.getMessage());
		}
	}

}
