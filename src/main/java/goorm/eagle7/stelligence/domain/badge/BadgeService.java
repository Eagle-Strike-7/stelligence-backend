package goorm.eagle7.stelligence.domain.badge;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.domain.badge.model.Badge;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.document.content.DocumentContentRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BadgeService {

	private final DocumentContentRepository documentContentRepository;
	private final ContributeRepository contributeRepository;

	public void getBadge(BadgeCategory badgeCategory, Member member) {

		Badge newBadge = null;

		// badgeCategory와 같은지 확인
		switch (badgeCategory) {
			case WRITING:
				// 글 작성
				newBadge = checkWritingConditionAndGetBadge(member, badgeCategory);
				break;
			case CONTRIBUTE:
				// 수정 요청
				newBadge = checkContributeConditionAndGetBadge(member, badgeCategory);
				break;
			case MEMBER_JOIN:
				// 회원 가입
				newBadge = checkMemberConditionAndGetBadge();
				break;
			case REPORT:
				// 신고 - 신고 테이블 따로 없어 미구현
				break;
		}

		if (newBadge != null) {
			member.addBadge(newBadge);
		}

	}

	private Badge checkWritingConditionAndGetBadge(Member member, BadgeCategory badgeCategory) {

		long count = documentContentRepository.countDistinctByAuthor_Id(member.getId());
		return Badge.findByEventCategoryAndCount(badgeCategory, count);

	}

	private Badge checkContributeConditionAndGetBadge(Member member, BadgeCategory badgeCategory) {

		long count = contributeRepository.countDistinctByMemberId(member.getId());
		return Badge.findByEventCategoryAndCount(badgeCategory, count);

	}

	// merge 추가 구현 필요


	private Badge checkMemberConditionAndGetBadge() {
		// 최초 회원 가입 검증 로직 따로 없어 미구현
		 return Badge.SPROUT;
	}

}
