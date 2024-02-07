package goorm.eagle7.stelligence.domain.badge;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.document.content.DocumentContentRepository;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BadgeService {

	private final DocumentContentRepository documentContentRepository;
	private final ContributeRepository contributeRepository;
	private final MemberRepository memberRepository;

	public void getBadge(BadgeCategory badgeCategory, Member member) {

		// badgeCategory와 같은지 확인
		switch (badgeCategory) {
			case WRITING:
				// 글 작성
				break;
			case CONTRIBUTE:
				// 수정 요청
				break;
			case MEMBER_JOIN:
				// 회원 가입
				break;
			case REPORT:
				// 신고
				break;
		}

		// 같은 badge를 찾고, count를 찾아 해당 count 이하인 badge를 member의 badges에 추가

	}
}
