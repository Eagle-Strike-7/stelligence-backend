package goorm.eagle7.stelligence.domain.withdrawnmember;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * <h2>탈퇴 회원 테이블 초기화</h2>
 * <p> - 탈퇴 회원 테이블이 없으면 생성, 회원 탈퇴 시, 회원 정보를 저장함.</p>
 * <p> - 탈퇴 회원 테이블: original_member_id, joined_at, withdrawn_at, name, nickname, email, social_id, social_type, role, image_url, contributes</p>
 * @see CommandLineRunner Spring Boot 실행 시점에 실행
 * @see JdbcTemplate JDBC 사용
 */
@Component
@RequiredArgsConstructor
public class WithdrawnTableInitializer implements CommandLineRunner {

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