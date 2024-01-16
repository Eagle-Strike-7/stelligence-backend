package goorm.eagle7.stelligence.common.auth.memberinfo;

import goorm.eagle7.stelligence.domain.member.model.Role;

/**
 * Filter에서 MemberInfo를 ThreadLocal에 저장하고, Controller에서 @Auth를 사용하면 ThreadLocal에 저장됐던 MemberInfo를 받을 수 있다.
 * - 웹 애플리케이션에서 각 요청이 별도의 스레드에서 처리될 수 있도록 하기 위해 ThreadLocal을 사용해 각 스레드에서 MemberInfo를 독립적으로 관리.
 * - ThreadLocal은 인스턴스 자체가 아니라 현재 스레드에 대한 컨텍스트를 관리하기 위한 수단이기 때문에 빈으로 등록 X
 */
public class MemberContextHolder {
	private static final ThreadLocal<MemberInfo> CONTEXT = new ThreadLocal<>();

	/**
	 * ThreadLocal에 저장된 데이터를 반환함.
	 * @return MemberInfo null인 경우, 기본 객체 MemberInfo.of(1L, Role.USER)를 반환 - test 용, TODO 기본값은 MemberInfo.of(null, null)로 고려 중.
	 */
	public static MemberInfo getMemberInfo() {
		// TODO 임시로 1L, USER 반환, (null, null)은 고려 X
		return CONTEXT.get() == null ? MemberInfo.of(1L, Role.USER) : CONTEXT.get();
	}

	/**
	 * ThreadLocal에 MemberInfo를 저장함.
	 * @param memberInfo
	 * @return void
	 */
	public static void setMemberInfo(MemberInfo memberInfo) {
		CONTEXT.set(memberInfo);
	}

	/**
	 * ThreadLocal에 저장된 데이터를 삭제한다.
	 * 스프링은 쓰레드풀을 사용하므로 쓰레드 종료시 반드시 지워주는 작업이 필요
	 */
	public static void clear() {
		CONTEXT.remove();
	}

}
