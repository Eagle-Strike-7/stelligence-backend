package goorm.eagle7.stelligence.common.auth.memberinfo;

import goorm.eagle7.stelligence.domain.member.model.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * MemberInfoArgumentResolver를 이용해 @Auth 애노테이션 사용 시 반환하는 객체
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "of")
public class MemberInfo {
	private Long id;
	private Role role;
}
