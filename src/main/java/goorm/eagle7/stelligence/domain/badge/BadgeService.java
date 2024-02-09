package goorm.eagle7.stelligence.domain.badge;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.domain.badge.model.Badge;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.document.content.DocumentContentRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BadgeService {

	private final ContributeRepository contributeRepository;
	private final DocumentContentRepository documentContentRepository;

	@Transactional
	public void checkAndAwardBadge(BadgeCategory badgeCategory, Member member) {

		Badge newBadge = null;

		// badgeCategory와 같은지 확인
		switch (badgeCategory) {
			case WRITING:
				// 글 작성
				newBadge = checkWritingConditionAndGetBadge(member, badgeCategory);
				break;
			case CONTRIBUTE_ALL:
				// 모든 수정 요청
				newBadge = checkContributeAllAndGetBadge(member, badgeCategory);
				break;
			case CONTRIBUTE_MERGED:
				// 반영된 수정 요청
				newBadge = checkContributeMergedAndGetBadge(member, badgeCategory);
				break;
			case CONTRIBUTE_REJECTED:
				// 반려된 수정 요청
				newBadge = checkContributeRejectedAndGetBadge(member, badgeCategory);
				break;
			case REPORT:
				// 신고 - 신고 테이블 따로 없어 미구현
				break;
			case MEMBER_JOIN:
				// 회원 가입 - 바로 배지 발행해 미구현
				break;
		}

		if (newBadge != null) {
			member.addBadge(newBadge);
		}

	}

	private Badge checkWritingConditionAndGetBadge(Member member, BadgeCategory badgeCategory) {

		long count = documentContentRepository.countByAuthor_Id(member.getId());
		return Badge.findByEventCategoryAndCount(badgeCategory, count);

	}

	private Badge checkContributeAllAndGetBadge(Member member, BadgeCategory badgeCategory) {

		long count = contributeRepository.countByMemberId(member.getId());
		return Badge.findByEventCategoryAndCount(badgeCategory, count);

	}
	private Badge checkContributeMergedAndGetBadge(Member member, BadgeCategory badgeCategory) {
		long count = contributeRepository.countByMemberIdAndStatus(member.getId(), ContributeStatus.MERGED);
		return Badge.findByEventCategoryAndCount(badgeCategory, count);
	}

	private Badge checkContributeRejectedAndGetBadge(Member member, BadgeCategory badgeCategory) {
		long count = contributeRepository.countByMemberIdAndStatus(member.getId(), ContributeStatus.REJECTED);
		return Badge.findByEventCategoryAndCount(badgeCategory, count);
	}

}
