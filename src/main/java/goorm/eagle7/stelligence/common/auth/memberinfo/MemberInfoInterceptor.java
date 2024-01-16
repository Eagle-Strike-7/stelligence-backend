package goorm.eagle7.stelligence.common.auth.memberinfo;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import goorm.eagle7.stelligence.domain.member.model.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * AuthFilter에서 검증된 MemberInfo를 ThreadLocal에 저장
 */
@Component
public class MemberInfoInterceptor implements HandlerInterceptor {

	// TODO 실제 Filter에서 거르고 들어온다고 하면, MemberInfo에도 다 똑같이 넣어줘야 하나? 애노테이션은 쓸 곳에서 쓰고, Resources에 추가해 주면 괜찮은 건가?
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
		Exception {

		// ThreadLocal 초기화
		MemberContextHolder.clear();

		// TODO Test 시 Filter 사용하지 않으면 @Auth 사용 시 MemberInfo가 없음 -> 테스트용 MemberInfo 생성하여 저장
		MemberInfo memberInfo =
			(MemberInfo)request.getAttribute("memberInfo") == null ?
				MemberInfo.of(1L, Role.USER) :
				(MemberInfo)request.getAttribute("memberInfo");
		MemberContextHolder.setMemberInfo(memberInfo);

		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
		Exception ex) throws Exception {
		MemberContextHolder.clear();
	}
}
