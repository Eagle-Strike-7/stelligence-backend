package goorm.eagle7.stelligence.domain.notification.custom;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CustomNotificationRepositoryImpl implements CustomNotificationRepository {

	private final JdbcTemplate jdbcTemplate;

	private static final String INSERT_NOTIFICATIONS_SQL = "INSERT INTO notification (notification_content_id, member_id, is_read, created_at, updated_at) VALUES (?, ?, false, NOW(), NOW())";
	private static final String INSERT_NOTIFICATION_CONTENT_SQL = "INSERT INTO notification_content (message, uri, created_at, updated_at) VALUES (?, ?, NOW(), NOW())";

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
	@Transactional
	public void insertNotifications(String message, String uri, Set<Long> memberIds) {
		// NOTIFICATION_CONTENT 테이블에 알림내용 등록 후 생성된 ID 가져오기
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(connection -> {
			var ps = connection.prepareStatement(INSERT_NOTIFICATION_CONTENT_SQL,
				Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, message);
			ps.setString(2, uri);
			return ps;
		}, keyHolder);

		long notificationContentId = Objects.requireNonNull(keyHolder.getKey()).longValue();

		// batchUpdate를 사용하기 위해 파라미터 리스트 생성

		List<Object[]> parameters = new ArrayList<>();
		for (Long memberId : memberIds) {
			parameters.add(new Object[] {notificationContentId, memberId});
		}

		// batchUpdate를 사용하여 여러 개의 알림을 한 번에 등록
		jdbcTemplate.batchUpdate(INSERT_NOTIFICATIONS_SQL, parameters);
	}
}
