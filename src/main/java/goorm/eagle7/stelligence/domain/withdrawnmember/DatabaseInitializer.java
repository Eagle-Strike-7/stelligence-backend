package goorm.eagle7.stelligence.domain.withdrawnmember;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

	private final JdbcTemplate jdbcTemplate;

	@Override
	public void run(String... args) {
		String sql = """
			CREATE TABLE IF NOT EXISTS withdrawn_member (
				withdrawn_member_id BIGINT PRIMARY KEY AUTO_INCREMENT,
				original_member_id BIGINT NOT NULL,
				joined_at DATETIME(6) NOT NULL,
				withdrawn_at DATETIME(6) NOT NULL,
			    name VARCHAR(255) NULL,
			    nickname VARCHAR(255) NULL,
			    email VARCHAR(255) NULL,
			    social_id VARCHAR(255) NULL,
			    social_type ENUM('GOOGLE', 'NAVER', 'KAKAO') NULL,
			    role ENUM('ADMIN', 'USER') NULL,
			    image_url VARCHAR(255) NULL,
			    contributes BIGINT NULL
			)
			""";

		jdbcTemplate.execute(sql);
	}
}