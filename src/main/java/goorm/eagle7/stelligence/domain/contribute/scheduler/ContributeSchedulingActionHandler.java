package goorm.eagle7.stelligence.domain.contribute.scheduler;

import org.springframework.transaction.annotation.Transactional;

/**
 * ContributeSchedulingAction에 따라 적합한 행동을 취하는 핸들러의 인터페이스입니다.
 */
public interface ContributeSchedulingActionHandler {

	/**
	 * ContributeSchedulingAction에 따라 적합한 행동을 수행합니다.
	 *
	 * <p> <b>@Transactional</b>: Scheduler에 의해서 호출되는 handle 메서드는 각각의 동작이
	 * 다른 contribute를 수행하는 동작과는 분리된 단위로 관리되어야 합니다.
	 *
	 * <p> ex) 10개의 수정요청을 반영하는 동작들 중에 하나가 실패하더라도
	 * 나머지는 성공적으로 수행된다면 나머지는 모두 반영되어야 합니다.
	 *
	 * <p>스프링 과거 버전에서는 CGLIB을 통한 프록시 생성 방식에서는 인터페이스 레벨의 @Transactional이 무시되었으나
	 * 스프링 5버전 이후 개선되어 인터페이스 레벨의 @Transactional도 적용됩니다.
	 *
	 * <p>그럼에도 불구하고
	 *    <a href="https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html">
	 *        스프링 공식문서
	 *    </a>
	 * 에서는 구체 클래스에 @Transactional 을 붙이도록 권장하고 있는데,
	 * 이는 AOP 구현체에 따라 다르게 동작할 가능성이 있기 때문입니다.
	 *
	 * @param contributeId 수행 대상 Contribute의 id
	 */
	@Transactional
	void handle(Long contributeId);
}
