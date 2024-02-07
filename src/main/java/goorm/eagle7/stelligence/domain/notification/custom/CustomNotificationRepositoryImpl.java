package goorm.eagle7.stelligence.domain.notification.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CustomNotificationRepositoryImpl implements CustomNotificationRepository {

	private final JdbcTemplate jdbcTemplate;

	private static final String INSERT_NOTIFICATIONS_SQL = "INSERT INTO notification (message, uri, member_id, is_read, created_at, updated_at) VALUES (?,?,?, false, NOW(), NOW())";

	/**
	 * 알림 등록
	 * <p>JdbcTemplate의 batchUpdate를 사용하여 여러 개의 알림을 한 번에 등록합니다.  참고
	 * memberId에 대한 검증이 이루어지지 않으므로, 사용하는 측에서 철저히 검증해야 합니다.
	 *
	 * <p>혹시나 알림을 생성하는데에 실패한 경우 (주로 memberId가 존재하지 않는 경우) 모든 알림이 전송되지 않습니다.
	 * 이 경우 개별적으로 알림을 생성하도록 시도합니다.
	 *
	 * <p> <a href="https://hyos-dev-log.tistory.com/1">batchUpdate</a> batchUpdate는 DB Driver와
	 * 환경변수 속성에 따라 다르게 동작합니다. MySQL의 경우 rewriteBatchedStatements=true로 설정하면 여러행을 삽입하는
	 * SQL이 생성되며, 해당 속성이 없다면 각각의 SQL을 실행합니다. 이 때문에 성능상의 차이가 발생할 수 있습니다.
	 *
	 * @param message 알림 메시지
	 * @param uri 알림 링크
	 * @param memberIds 알림 대상 회원 목록
	 */
	@Override
	public void insertNotifications(String message, String uri, Set<Long> memberIds) {
		List<Object[]> parameters = new ArrayList<>();
		for (Long memberId : memberIds) {
			parameters.add(new Object[] {message, uri, memberId});
		}

		try {
			// batchUpdate를 사용하여 여러 개의 알림을 한 번에 등록
			jdbcTemplate.batchUpdate(INSERT_NOTIFICATIONS_SQL, parameters);

		} catch (DataIntegrityViolationException e) {
			// memberId가 존재하지 않는 경우 등록에 실패할 수 있음
			log.error("알림 등록 중 오류가 발생했습니다. {}", e.getMessage());

			log.info("개별 알림 등록을 시도합니다.");
			for (Object[] parameter : parameters) {
				try {
					jdbcTemplate.update(INSERT_NOTIFICATIONS_SQL, parameter);
				} catch (DataIntegrityViolationException e2) {
					log.error("알림 등록 중 오류가 발생했습니다. MEMBER ID : {}", parameter[2]);
				}
			}
		}
	}
}
