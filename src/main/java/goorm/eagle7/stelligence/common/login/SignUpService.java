package goorm.eagle7.stelligence.common.login;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import goorm.eagle7.stelligence.common.login.dto.LoginOAuth2Request;
import goorm.eagle7.stelligence.common.util.RandomUtils;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SignUpService {

	private final MemberRepository memberRepository;

	@Transactional
	public Member oauth2SignUp(LoginOAuth2Request loginOAuth2Request) {

		String baseNickname = loginOAuth2Request.getNickname();

		// 닉네임이 중복인지 확인, 중복이면 랜덤 닉네임 생성
		String uniqueNickname = RandomUtils.generateUniqueNickname(baseNickname, () -> isNicknameDuplicate(baseNickname));

		Member newMember = Member.of(
			null,
			uniqueNickname,
			loginOAuth2Request.getEmail(),
			loginOAuth2Request.getImageUrl(),
			loginOAuth2Request.getSocialId(),
			loginOAuth2Request.getSocialType()
		);

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
