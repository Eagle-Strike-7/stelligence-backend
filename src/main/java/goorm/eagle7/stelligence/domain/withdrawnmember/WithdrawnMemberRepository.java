package goorm.eagle7.stelligence.domain.withdrawnmember;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WithdrawnMemberRepository {

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private static final String SQL = """
		INSERT INTO withdrawn_member (
		    original_member_id,
		    joined_at,
		    withdrawn_at,
		    nickname,
		    email,
		    social_id,
		    social_type,
		    role,
		    image_url,
		    contributes
		) VALUES (
		    :originalMemberId, 
		    :joinedAt, 
		    :withdrawnAt, 
		    :nickname, 
		    :email, 
		    :socialId, 
		    :socialType, 
		    :role, 
		    :imageUrl, 
		    :contributes
		)
		""";

	public void insertWithdrawnMember(Member member) {

		MapSqlParameterSource parameters = new MapSqlParameterSource()
			.addValue("originalMemberId", member.getId())
			.addValue("joinedAt", Timestamp.valueOf(member.getCreatedAt()))
			.addValue("withdrawnAt", Timestamp.valueOf(LocalDateTime.now()))
			.addValue("nickname", member.getNickname())
			.addValue("email", member.getEmail())
			.addValue("socialId", member.getSocialId())
			.addValue("socialType", member.getSocialType().name())
			.addValue("role", member.getRole().name())
			.addValue("imageUrl", member.getImageUrl())
			.addValue("contributes", member.getContributes());

		namedParameterJdbcTemplate.update(SQL, parameters);

	}
}