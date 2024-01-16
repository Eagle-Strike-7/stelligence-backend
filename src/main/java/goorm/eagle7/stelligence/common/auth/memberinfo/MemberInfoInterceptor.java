package goorm.eagle7.stelligence.common.auth.memberinfo;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import goorm.eagle7.stelligence.domain.member.model.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * AuthFilter에서 검증된 MemberInfo를 ThreadLocal에 저장, 전달
 */
@Slf4j
@Component
public class MemberInfoInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

		// ThreadLocal 초기화
		MemberContextHolder.clear();

		// test 용으로 1L, USER 반환
		// Test 시 Filter 사용하지 않으면 @Auth 사용 시 MemberInfo가 없음 -> 테스트용 MemberInfo 생성하여 저장
		// 운영 시 기본값은 MemberInfo.of(null, null)가 낫다고 함.
		MemberInfo memberInfo =
			(MemberInfo) request.getAttribute("memberInfo") == null ?
				MemberInfo.of(1L, Role.USER) :
				(MemberInfo)request.getAttribute("memberInfo");
		MemberContextHolder.setMemberInfo(memberInfo);

		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
		Exception ex) {
		MemberContextHolder.clear();
	}
}
