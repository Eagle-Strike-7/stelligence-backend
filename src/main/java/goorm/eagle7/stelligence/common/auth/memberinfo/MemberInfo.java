package goorm.eagle7.stelligence.common.auth.memberinfo;

import goorm.eagle7.stelligence.domain.member.model.Role;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <h2>로그인한 회원 정보</h2>
 * <p>MemberInfoArgumentResolver를 이용해 @Auth 애노테이션 사용 시 반환하는 객체</p>
 * <p>MemberInfoContextHolder에 저장해 놓음.</p>
 * <p>로그인하지 않은 경우(쿠키가 없는 경우), null 반환.</p>
 */
@Hidden // Swagger 문서에 노출되지 않도록 설정
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "of")
public class MemberInfo {
	private Long id;
	private Role role;
}
