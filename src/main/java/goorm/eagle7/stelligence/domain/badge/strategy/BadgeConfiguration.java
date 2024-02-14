package goorm.eagle7.stelligence.domain.badge.strategy;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;

@Configuration
public class BadgeConfiguration {

	/**
	 * <h2>BadgeCategory, BadgeAwardStrategy 매핑</h2>
	 * <p>- BadgeAwardStrategy 구현체들을 주입받아서 BadgeCategory에 따라 매핑하여 Map으로 반환</p>
	 * <p>- EnumMap: Enum을 key로 사용, 키의 순서를 Enum 내의 선언 순서대로 유지, null 허용 X</p>
	 * <p>- EnumMap 내부적으로 Enum의 ordinal 값을 인덱스로 사용하여 요소를 저장해 HashMap보다 더 빠름</p>
	 * @param strategies BadgeAwardStrategy 구현체들
	 * @return Map BadgeCategory에 따라 매핑된 BadgeAwardStrategy Map
	 */
	@Bean
	public Map<BadgeCategory, BadgeAwardStrategy> badgeAwardStrategyMap(List<BadgeAwardStrategy> strategies) {

		Map<BadgeCategory, BadgeAwardStrategy> strategyMap = new EnumMap<>(BadgeCategory.class);

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

}