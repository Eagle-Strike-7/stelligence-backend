package goorm.eagle7.stelligence.domain.badge;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.domain.badge.model.Badge;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.badge.strategy.BadgeAwardStrategy;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BadgeService {

	private final Map<BadgeCategory, BadgeAwardStrategy> strategyMap = new HashMap<>(); // TODO enunMap으로 변경 확인

	public BadgeService(List<BadgeAwardStrategy> strategies) {
		strategies.forEach(strategy ->
			Arrays.stream(BadgeCategory.values())
				.filter(strategy::supports)
				.forEach(category -> strategyMap.put(category, strategy)));
	}

	@Transactional
	public void checkAndAwardBadge(BadgeCategory badgeCategory, Member member) {

		BadgeAwardStrategy strategy = strategyMap.get(badgeCategory);
		if (strategy == null) {
			throw new IllegalArgumentException("Unsupported badge category: " + badgeCategory);
		}
		Badge newBadge = strategy.checkAndAward(badgeCategory, member);
		if (newBadge != null) {
			member.addBadge(newBadge);
		}
	}

}
