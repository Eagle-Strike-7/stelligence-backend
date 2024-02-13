package goorm.eagle7.stelligence.common.login;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

		// 닉네임이 중복인지 확인, 중복이면 랜덤 닉네임 생성
		String uniqueNickname = loginOAuth2Request.getNickname();

		// RandomUtils 내에서 null 확인 후 기본값으로 랜덤 닉네임 생성
		uniqueNickname = RandomUtils.generateUniqueNickname(uniqueNickname, this::isNicknameDuplicate);

		Member newMember = Member.of(
			loginOAuth2Request.getName(),
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
		return memberRepository.existsByNickname(nickname);
	}

}
