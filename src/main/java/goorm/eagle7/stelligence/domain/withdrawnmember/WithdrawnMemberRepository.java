package goorm.eagle7.stelligence.domain.withdrawnmember;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WithdrawnMemberRepository {

	private final JdbcTemplate jdbcTemplate;

	public void insertWithdrawnMember(Member member) {
		String sql = "INSERT INTO withdrawn_member (original_member_id, joined_at, withdrawn_at, name, nickname, email, social_id, social_type, role, image_url, contributes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		jdbcTemplate.update(sql,
			member.getId(),
			Timestamp.valueOf(member.getCreatedAt()),
			LocalDateTime.now(),
			member.getName(),
			member.getNickname(),
			member.getEmail(),
			member.getSocialId(),
			member.getSocialType().name(),
			member.getRole().name(),
			member.getImageUrl(),
			member.getContributes());

	}
}