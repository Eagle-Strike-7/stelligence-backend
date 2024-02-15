package goorm.eagle7.stelligence.domain.badge.strategy;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BadgeTemplateMatcher {

	private final Map<BadgeCategory, BadgeAwardStrategyTemplate> strategyMap = new EnumMap<>(BadgeCategory.class);

	/**
	 * <h2>BadgeCategory, BadgeAwardStrategy 매핑</h2>
	 * <p>- BadgeAwardStrategy 구현체들을 주입받아서 BadgeCategory에 따라 매핑하여 Map으로 반환</p>
	 * <p>- EnumMap: Enum을 key로 사용, 키의 순서를 Enum 내의 선언 순서대로 유지, null 허용 X</p>
	 * <p>- EnumMap 내부적으로 Enum의 ordinal 값을 인덱스로 사용하여 요소를 저장해 HashMap보다 더 빠름</p>
	 * @param strategies BadgeAwardStrategy 구현체들
	 * @return Map BadgeCategory에 따라 매핑된 BadgeAwardStrategy Map
	 */
	@Bean
	private Map<BadgeCategory, BadgeAwardStrategyTemplate> findStrategy(
		List<BadgeAwardStrategyTemplate> strategies) {

		// 각 BadgeAwardStrategy 구현체들을 BadgeCategory에 따라 매핑
		strategies
			.forEach(strategy ->
				Arrays.stream(BadgeCategory.values())
					.filter(strategy::supports)
					.forEach(category ->
						strategyMap.put(category, strategy)
					)
			);

		return strategyMap;
	}

	public BadgeAwardStrategyTemplate findStrategy(BadgeCategory badgeCategory) {
		return strategyMap.get(badgeCategory);
	}

}