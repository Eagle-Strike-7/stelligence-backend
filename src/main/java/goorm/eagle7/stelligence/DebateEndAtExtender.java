package goorm.eagle7.stelligence;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * 개발 테스트 용으로 특정 토론을 유지시키기 위한 스케쥴러입니다.
 */
@Component
@RequiredArgsConstructor
public class DebateEndAtExtender {

	private final JdbcTemplate jdbcTemplate;

	@Scheduled(fixedRate = 1000 * 60 * 3) // 3분마다 실행
	public void extendDebateEndAt() {
		//now + 15로 endat을 연장한다.
		jdbcTemplate.update("UPDATE debate SET end_at = DATE_ADD(NOW(), INTERVAL 300 MINUTE) WHERE debate_id = 4");
	}

}
