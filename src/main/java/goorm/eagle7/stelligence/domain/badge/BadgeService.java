package goorm.eagle7.stelligence.domain.badge;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.badge.strategy.BadgeAwardStrategy;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BadgeService {

	private final Map<BadgeCategory, BadgeAwardStrategy> strategyMap;

	/**
	 * <h2>BadgeCategory에 해당하는 전략을 찾아서 수행</h2>
	 * @param badgeCategory BadgeCategory
	 * @param member Member
	 * @throws IllegalArgumentException BadgeCategory에 해당하는 전략이 없을 경우
	 */
	@Transactional
	public void checkAndAwardBadge(BadgeCategory badgeCategory, Member member) {

		// badgeCategory에 해당하는 전략 category 찾기
		BadgeAwardStrategy strategy = strategyMap.get(badgeCategory);
		if (strategy == null) {
			// BadgeCategory에 해당하는 전략이 없을 경우는 server error로 판단
			throw new IllegalArgumentException("Unsupported badge category: " + badgeCategory);
		}

		// 전략 상세 조건 확인 후 발급
		strategy.checkAndAward(member);

	}

}
