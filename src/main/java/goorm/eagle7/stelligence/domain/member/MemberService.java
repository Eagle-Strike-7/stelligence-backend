package goorm.eagle7.stelligence.domain.member;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.member.dto.MemberMiniProfileResponse;
import goorm.eagle7.stelligence.domain.member.dto.MemberProfileResponse;
import goorm.eagle7.stelligence.domain.member.dto.MemberUpdateNicknameRequest;
import goorm.eagle7.stelligence.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private static final String NOT_FOUND_MEMBER_EXCEPTION_MESSAGE =  "해당 멤버를 찾을 수 없습니다. MemberId= %s"; // 서식 문자 사용

	// TODO 401 error - 프론트와 어떤 uri가 로그인 필요한 건지 다시 한번 협의
	/**
	 * <h2>회원 프로필 조회</h2>
	 * <p>프로필: 닉네임, 이메일, 프로필 사진, 가입한 소셜 타입</p>
	 * @param memberId 회원 id
	 * @return MemberMyPageResponse 닉네임, 이메일, 프로필 사진, 가입한 소셜 타입
	 * @throws BaseException 회원을 찾을 수 없는 경우 400
	 * @see MemberService#findMemberById(Long)
	 */
	public MemberProfileResponse getProfileById(Long memberId) {
		Member member = findMemberById(memberId);
		return MemberProfileResponse.from(member);
	}

	/**
	 * <h2>회원 탈퇴 요청 시 회원 삭제</h2>
	 * <p>- 회원 삭제 시, 글 제외한 해당 회원만 삭제.</p> TODO 모든 정보인지, null 처리 등 확인 필요.
	 * <p>- 존재하지 않는 회원 id여도 Exception은 발생하지 않음.</p>
	 * @param memberId 회원 id
	 */ // TODO 탈퇴 시 다른 DB에 저장, 기존은 기본 값 설정 혹은 findById 등으로 조회할 때 null이면 기본값 처리 등.
	@Transactional
	public void delete(Long memberId) {
		memberRepository.deleteById(memberId);
	}

	/**
	 * <h2>회원 닉네임 수정</h2>
	 * <p>- 닉네임 중복 시 예외 발생</p>
	 * <p>- 중복이 아니라면 해당 닉네임으로 닉네임 변경</p>
	 * @param memberId 회원 id
	 * @param memberUpdateNicknameRequest 수정할 닉네임
	 * @throws BaseException 회원을 찾을 수 없는 경우 400, TODO 이미 사용 중인 닉네임인 경우 409
	 * @see MemberService#findMemberById(Long)
	 */
	@Transactional
	public void updateNickname(Long memberId, MemberUpdateNicknameRequest memberUpdateNicknameRequest) {
		// TODO 409 Error 혹은 닉네임 중복 검사 논의 필요.
		String nickname = memberUpdateNicknameRequest.getNickname();
		if (memberRepository.existsByNickname(nickname)) {
			// 이미 사용 중인 닉네임이면 예외 발생
			throw new BaseException("이미 사용 중인 닉네임입니다. nickname=" + nickname);
		}
		// 사용 중이지 않은 닉네임이면 닉네임 변경
		Member member = findMemberById(memberId);
		member.updateNickname(nickname);
	}

	/**
	 * <h2>회원의 mini profile 조회</h2>
	 * <p>- mini profile: 닉네임, 프로필 사진 Url</p>
	 * @param memberId 회원 id
	 * @return MemberMiniProfileResponse (닉네임, 프로필 사진)
	 * @throws BaseException 회원을 찾을 수 없는 경우 400
	 * @see MemberService#findMemberById(Long)
	 */
	public MemberMiniProfileResponse getMiniProfileById(Long memberId) {
		Member member = findMemberById(memberId);
		return MemberMiniProfileResponse.of(
			member.getNickname(),
			member.getImageUrl()
		);
	}

	/**
	 * <h2>회원 id로 회원 조회</h2>
	 * <p>해당 요구 사항에 대한 일관적인 Exception 유지 및 코드 중복 최소화를 위해 메서드 분리.</p>
	 * @param memberId 회원 id
	 * @return Member 회원
	 * @throws BaseException 회원을 찾을 수 없는 경우 400
	 */
	private Member findMemberById(Long memberId) {
		return memberRepository.findById(memberId).orElseThrow(
			() -> new BaseException(String.format(NOT_FOUND_MEMBER_EXCEPTION_MESSAGE, memberId))
		);
	}
}
