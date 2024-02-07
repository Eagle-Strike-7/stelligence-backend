package goorm.eagle7.stelligence.common.login;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.member.model.SocialType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SignUpService {

	private final MemberRepository memberRepository;

	public Member signUp(String nickname) {

		// 닉네임이 중복인지 확인, 중복이면 랜덤 닉네임 생성
		if (isNicknameDuplicate(nickname)) {
			nickname = generateUniqueNickname(nickname);
		}

		// member 생성 OAuth2.0 테스트용 하드 코딩
		Member newMember = Member.of("영민", nickname, "sbslc2000@stelligence.com", "star.com",
			"eeunzzi", SocialType.KAKAO);

		// 해당 닉네임으로 저장
		return memberRepository.save(newMember);

	}

	// 닉네임 중복 확인 메서드
	private boolean isNicknameDuplicate(String nickname) {
		return memberRepository.existsByNicknameAndActiveTrue(nickname);
	}

	// 닉네임 생성 메서드
	public String generateUniqueNickname(String nickname) {
		while (isNicknameDuplicate(nickname)) {
			nickname = RandomUtils.createNicknameWithRandomNumber(nickname);
		}
		return nickname;
	}

}
