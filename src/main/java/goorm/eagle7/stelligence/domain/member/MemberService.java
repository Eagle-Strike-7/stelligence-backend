package goorm.eagle7.stelligence.domain.member;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.member.dto.MemberBadgesResponse;
import goorm.eagle7.stelligence.domain.member.dto.MemberDetailResponse;
import goorm.eagle7.stelligence.domain.member.dto.MemberSimpleResponse;
import goorm.eagle7.stelligence.domain.member.dto.MemberUpdateNicknameRequest;
import goorm.eagle7.stelligence.domain.member.model.Member;
// import goorm.eagle7.stelligence.domain.withdrawnmember.WithdrawnMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	// private final WithdrawnMemberRepository withdrawnMemberRepository;
	private static final String NOT_FOUND_MEMBER_EXCEPTION_MESSAGE = "해당 멤버를 찾을 수 없습니다. MemberId= %s"; // 서식 문자 사용

	// TODO 401 error - 프론트와 어떤 uri가 로그인 필요한 건지 다시 한번 협의
	/**
	 * <h2>회원 프로필 조회</h2>
	 * <p>프로필: 닉네임, 이메일, 프로필 사진, 가입한 소셜 타입</p>
	 * @param memberId 회원 id
	 * @return MemberMyPageResponse 닉네임, 이메일, 프로필 사진, 가입한 소셜 타입
	 * @throws BaseException 회원을 찾을 수 없는 경우 400
	 * @see MemberService#findMemberById(Long)
	 */
	public MemberDetailResponse getProfileById(Long memberId) {
		Member member = findMemberById(memberId);
		return MemberDetailResponse.from(member);
	}

	/**
	 * <h2>회원 탈퇴 요청 시 회원 삭제</h2>
	 * <p>- 글 제외한 해당 회원만 soft delete.</p>
	 * <p>- 탈퇴한 회원 Table로 따로 저장. </p>
	 * <p>- 해당 회원의 닉네임을 탈퇴한 회원NeutronStar{id}로 변경.</p>
	 * @param memberId 회원 id
	 */
	@Transactional
	public void delete(Long memberId) {
		Member member = memberRepository.findById(memberId).orElseThrow(
			() -> new BaseException(String.format(NOT_FOUND_MEMBER_EXCEPTION_MESSAGE, memberId))
		);
		// withdrawnMemberRepository.insertWithdrawnMember(member);
		// String nickname = "탈퇴한 회원NeutronStar"+ member.getId();
		// member.withdraw();
		// member.updateNickname(nickname);
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

		// 현재 있는 member인지 확인 // TODO existsByNickname() 와 findById() 순서 - 500 error
		Member member = findMemberById(memberId);

		// nickname 검사
		String nickname = memberUpdateNicknameRequest.getNickname();
		if (memberRepository.existsByNickname(nickname)) {
			// 이미 사용 중인 닉네임이면 예외 발생
			throw new BaseException("이미 사용 중인 닉네임입니다. nickname=" + nickname);
		}

		// 사용 중이지 않은 닉네임이면 닉네임 변경
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
	public MemberSimpleResponse getMiniProfileById(Long memberId) {
		Member member = findMemberById(memberId);
		return MemberSimpleResponse.of(
			member.getId(),
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
	 */ //TODO 400 / 500 error
	private Member findMemberById(Long memberId) {
		return memberRepository.findById(memberId).orElseThrow(
			() -> new BaseException(String.format(NOT_FOUND_MEMBER_EXCEPTION_MESSAGE, memberId))
		);
	}

	/**
	 * <h2>회원이 획득한 badge 목록 조회</h2>
	 * @param memberId 회원 id
	 * @return  MemberBadgesListResponse 회원이 획득한 badge 목록
	 * @throws BaseException 회원을 찾을 수 없는 경우 400
	 */
	// public MemberBadgesListResponse getBadgesById(Long memberId) {
	//
	// 	Member member = findMemberById(memberId);
	// 	Set<Badge> badges = member.getBadges();
	// 	List<MemberBadgesResponse> list = badges.stream().map(MemberBadgesResponse::from).toList();
	//
	// 	return MemberBadgesListResponse.from(list);
	//
	// }

}
