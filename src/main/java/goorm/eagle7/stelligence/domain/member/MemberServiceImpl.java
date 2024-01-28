package goorm.eagle7.stelligence.domain.member;

import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.member.dto.MemberBadgesResponse;
import goorm.eagle7.stelligence.domain.member.dto.MemberMiniProfileResponse;
import goorm.eagle7.stelligence.domain.member.dto.MemberMyPageResponse;
import goorm.eagle7.stelligence.domain.member.dto.MemberUpdateNicknameRequest;
import goorm.eagle7.stelligence.domain.member.model.Badge;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

	private final MemberRepository memberRepository;
	private final static String EXCEPTION_MSG = "해당 멤버가 없습니다. id=";

	@Override
	public MemberMyPageResponse getMyPageById(Long id) {
		Member member = memberRepository.findById(id).orElseThrow(
			() -> new BaseException(EXCEPTION_MSG + id)
		);
		return MemberMyPageResponse.from(member);
	}

	@Override
	@Transactional
	public void delete(Long memberId) {
		memberRepository.deleteById(memberId);
	}

	@Override
	@Transactional
	public void updateNickname(Long memberId, MemberUpdateNicknameRequest memberUpdateNicknameRequest) {

		String nickname = memberUpdateNicknameRequest.getNickname();
		memberRepository.findByNickname(nickname)
			.ifPresentOrElse(
				m -> {
					// 이미 사용 중인 닉네임이면 예외 발생
					throw new BaseException("이미 사용 중인 닉네임입니다. nickname=" + nickname);
				},
				() -> {
					// 사용 중이지 않은 닉네임이면 닉네임 변경
					Member member = memberRepository.findById(memberId).orElseThrow(
						() -> new BaseException(EXCEPTION_MSG + memberId)
					);
					member.updateNickname(nickname);
				}
			);
	}


	@Override
	public MemberMiniProfileResponse getMiniProfileById(Long memberId) {
		Member member = memberRepository.findById(memberId).orElseThrow(
			() -> new BaseException(EXCEPTION_MSG + memberId));
		return MemberMiniProfileResponse.of(
			member.getNickname(),
			member.getImageUrl()
		);
	}
}
