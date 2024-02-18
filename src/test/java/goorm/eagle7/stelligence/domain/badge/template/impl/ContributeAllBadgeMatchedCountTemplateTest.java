package goorm.eagle7.stelligence.domain.badge.template.impl;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator;
import goorm.eagle7.stelligence.domain.badge.model.Badge;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;

@ExtendWith(MockitoExtension.class)
class ContributeAllBadgeMatchedCountTemplateTest {

	@Mock
	private ContributeRepository contributeRepository;
	@InjectMocks
	private ContributeAllBadgeMatchedCountTemplate template;

	private static final Map<Integer, Badge> requiredCounts = new HashMap<>();

	@Test
	@DisplayName("[성공] CONTRIBUTE_ALL인 경우, true 반환 - supports")
	void supports() {
	}

	@Test
	@DisplayName("[성공] CONTRIBUTE_ALL 외, false 반환 - supports")
	void supportsFalse() {
	}

	/** getCount **/
	@Test
	@DisplayName("[성공] repository에서 Member 별 달성한 조건 개수 가져오기 - getCount")
	void getCount() {
	}

	@Test
	@DisplayName("[확인] 0개인 경우, repository에서 Member 별 달성한 조건 개수 가져오기 - getCount")
	void getCount0() {
	}

	@Test
	@DisplayName("[성공] map에 저장된 배지 조건 얻기 - getBadgeCriteria")
	void getBadgeCriteria() {
	}

	@Test
	@DisplayName("[확인] 2번 이후 시간 확인, map에 저장된 배지 조건 얻기 - getBadgeCriteria")
	void getBadgeCriteria2() {
	}

	@Test
	@DisplayName("[성공] 동시성 확인, map에 저장된 배지 조건 얻기 - getBadgeCriteria")
	void getBadgeCriteriaConcurrent() {
	}

	/** getBadgeWithCount **/
	@Test
	@DisplayName("[성공] 0개, count 조건 별 충족하는 배지 찾기 - getBadgeWithCount")
	void getBadgeWithCountUnder() {

		// given
		ContributeAllBadgeMatchedCountTemplate spy = spy(template);
		Member member = member(1L, "nickname");
		when(spy.getCount(member.getId())).thenReturn(0L);

		// when
		Optional<Badge> badge = spy.getBadgeWithCount(member);

		// then
		assertThat(spy.getBadgeWithCount(member)).isEmpty();
		assertThat(badge).isEmpty();
		// verify(spy, times(1)).getCount(member.getId());
		// verify(spy, times(1)).getBadgeCriteria();


	}

	@Test
	@DisplayName("[성공] 1개(충족), count 조건 별 충족하는 배지 찾기 - getBadgeWithCount")
	void getBadgeWithCountExact() {
	}

	@Test
	@DisplayName("[성공] 4개(사잇값), count 조건 별 충족하는 배지 찾기 - getBadgeWithCount")
	void getBadgeWithCountMid() {
	}

	@Test
	@DisplayName("[성공] 51개(초과), count 조건 별 충족하는 배지 찾기 - getBadgeWithCount")
	void getBadgeWithCountOver() {
	}
}