package goorm.eagle7.stelligence.common.dev;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import goorm.eagle7.stelligence.common.dev.dto.DevLoginRequest;
import goorm.eagle7.stelligence.common.util.RandomUtils;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.member.model.SocialType;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DevSignUpService {

	private final MemberRepository memberRepository;

	@Transactional
	public Member devSignUp(DevLoginRequest loginRequest) {

		String baseNickname = loginRequest.getNickname();

		// 닉네임이 중복인지 확인, 중복이면 랜덤 닉네임 생성
		String uniqueNickname = RandomUtils.generateUniqueNickname(baseNickname, () -> isNicknameDuplicate(baseNickname));

		// member 생성 OAuth2.0 테스트용 하드 코딩
		Member newMember = Member.of( uniqueNickname, "sbslc2000@stelligence.com", "youngandmini.com",
			"eunzzi"+uniqueNickname, SocialType.KAKAO);

		// 해당 닉네임으로 저장
		return memberRepository.save(newMember);

	}

	// 닉네임 중복 확인 메서드
	private boolean isNicknameDuplicate(String nickname) {
		if(!StringUtils.hasText(nickname)) {
			return false;
		}
		return memberRepository.existsByNicknameAndActiveTrue(nickname);
	}

}
