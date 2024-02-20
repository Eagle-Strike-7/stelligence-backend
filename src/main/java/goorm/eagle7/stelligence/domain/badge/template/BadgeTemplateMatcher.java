package goorm.eagle7.stelligence.domain.badge.template;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;

@Component
public class BadgeTemplateMatcher {

	private final Map<BadgeCategory, BadgeMatchedCountTemplate> templateMap = new EnumMap<>(BadgeCategory.class);

	/**
	 * <h2>BadgeCategory, BadgeAwardStrategy 매핑</h2>
	 * <p>- BadgeAwardStrategy 구현체들을 주입받아서 BadgeCategory에 따라 매핑하여 Map으로 반환</p>
	 * <p>- EnumMap: Enum을 key로 사용, 키의 순서를 Enum 내의 선언 순서대로 유지, null 허용 X</p>
	 * <p>- EnumMap 내부적으로 Enum의 ordinal 값을 인덱스로 사용하여 요소를 저장해 HashMap보다 더 빠름</p>
	 * @param templates BadgeAwardStrategy 구현체들
	 */
	public BadgeTemplateMatcher(
		List<BadgeMatchedCountTemplate> templates) {

		// 각 BadgeAwardStrategy 구현체들을 BadgeCategory에 따라 매핑
		templates
			.forEach(strategy ->
				Arrays.stream(BadgeCategory.values())
					.filter(strategy::supports)
					.forEach(category ->
						templateMap.put(category, strategy)
					)
			);
	}

	public BadgeMatchedCountTemplate findTemplate(BadgeCategory badgeCategory) {
		return templateMap.get(badgeCategory);
	}

}