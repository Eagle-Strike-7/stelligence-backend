package goorm.eagle7.stelligence.domain.member;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.common.login.CookieUtils;
import goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator;
import goorm.eagle7.stelligence.domain.member.dto.MemberSimpleResponse;
import goorm.eagle7.stelligence.domain.member.dto.MemberDetailResponse;
import goorm.eagle7.stelligence.domain.member.dto.MemberUpdateNicknameRequest;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.member.model.SocialType;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@Mock
	private CookieUtils cookieUtils; // doNothing()을 위해 필요, TODO 따로 test
	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private MemberService memberService;

	private Member stdMember;

	/**
	 * <h2>테스트에 사용할 Member 객체 생성</h2>
	 */
	@BeforeEach
	void setUp() {
		stdMember = TestFixtureGenerator.member(1L, "stdNickname");
	}

	/**
	 * <h2>[예외] private findMemberById() 우회 테스트</h2>
	 * <p>- 다른 메서드에서 예외 발생 혹은 Member 얻는 용도로 사용하는 private 메서드</p>
	 * <p>- getProfileById 메서드로 우회해 예외 발생 테스트 진행</p>
	 * <p>결과: 해당 멤버를 찾을 수 없을 때 BaseException 발생</p>
	 * <p>검증 방식: Repository 호출 횟수</p>
	 * @see MemberService#findMemberById(Long)
	 * @see MemberService#getProfileById(Long)
	 */
	@Test
	@DisplayName("[예외] 해당 멤버를 찾을 수 없을 때 BaseException 발생 - private findMemberById() ")
	void findMemberByIdThrows() {

		// given - Repository - findById 시 null 반환
		Long stdMemberId = stdMember.getId();
		when(memberRepository.findById(stdMemberId)).thenReturn(Optional.empty());

		// when

		// then
		// 존재하지 않는 멤버를 조회했을 때 발생하는 BaseException 및 메시지 확인
		assertThatThrownBy(() -> memberService.getProfileById(stdMemberId))
			.isInstanceOf(BaseException.class)
			.hasMessage("해당 멤버를 찾을 수 없습니다. MemberId= 1"); // 서식 문자 사용에 의존하지 않기 위해 하드 코딩, 1L은 1로 변환됨.

		// Repository의 findById()가 호출됐는지 확인 - 1번
		verify(memberRepository, times(1)).findById(stdMemberId);

	}

	/**
	 * <h2>[정상] Member를 반환하는 private 메서드 테스트</h2>
	 * <p>결과: MemberProfileResponse 반환</p>
	 * <p>검증 방식: repository 호출 횟수, 반환 타입의 필드 직접 비교</p>
	 * @see MemberService#getProfileById(Long)
	 */
	@Test
	@DisplayName("[정상] memberId로 MemberProfile 반환(private 메서드라 우회) - getProfileById")
	void getProfileById() {

		// given - Repository에서 findById()가 호출되면 stdMember를 반환하도록 설정
		when(memberRepository.findById(stdMember.getId())).thenReturn(Optional.of(stdMember));

		// when
		// MemberService의 getProfileById()가 실제 호출됐을 때 actualResponse 반환
		MemberDetailResponse actualResponse = memberService.getProfileById(stdMember.getId());

		// then
		// actualResponse의 필드와 기댓값 비교
		assertThat(actualResponse.getNickname()).isEqualTo("stdNickname");
		assertThat(actualResponse.getProfileImgUrl()).isEqualTo("imageUrl");
		assertThat(actualResponse.getEmail()).isEqualTo("email");
		assertThat(actualResponse.getSocialType()).isEqualTo(SocialType.KAKAO);

	}

	/**
	 * <h2>[정상] MemberId로 회원 탈퇴 테스트</h2>
	 * <p>결과: deleteById()가 호출되고, 아무 일도 일어나지 않음.</p>
	 * <p>검증 방식: deleteById() 호출 횟수</p>
	 * @see MemberService#delete(Long)
	 */
	@Test
	@DisplayName("[정상] memberId로 회원 탈퇴 - delete")
	void deleteExistentMember() {

		// given
		Long memberId = stdMember.getId();
		doNothing().when(memberRepository).deleteById(memberId);
		doNothing().when(cookieUtils).deleteCookieBy(any());

		// when
		memberService.delete(memberId);

		// then - 존재하는 memberId면 deleteById()가 호출되고, 아무 일도 일어나지 않음.
		verify(memberRepository, times(1)).deleteById(memberId);

	}

	/**
	 * <h2>[예외] 존재하지 않는 memberId에 대한 회원 탈퇴 테스트</h2>
	 * <p>결과: 존재하지 않더라도, deleteById()가 호출되고, 아무 일도 일어나지 않음.</p>
	 * <p>검증 방식: deleteById() 호출 횟수</p>
	 * @see MemberService#delete(Long)
	 */
	@Test
	@DisplayName("[예외] 존재하지 않는 memberId에 대한 회원 탈퇴 - delete")
	void deleteNonExistentMember() {

		// given - 존재하지 않는 memberId
		Long nonExistentMemberId = 999L;
		doNothing().when(cookieUtils).deleteCookieBy(any());

		// when
		memberService.delete(nonExistentMemberId);

		// then - 존재하지 않는 memberId여도 deleteById()가 호출되고, 아무 일도 일어나지 않음.
		verify(memberRepository, times(1)).deleteById(nonExistentMemberId);

	}

	/**
	 * <h2>[정상] 닉네임 수정 테스트</h2>
	 * <p>결과: updateNickname()가 호출되고, 닉네임이 변경됨.'</p>
	 * <p>검증 방식: 닉네임 변경 여부</p>
	 * @see MemberService#updateNickname(Long, MemberUpdateNicknameRequest)
	 */
	@Test
	@DisplayName("[정상] 닉네임 수정 - updateNickname")
	void updateNickname() {

		// given - 중복이 아닌, 새로운 닉네임
		Long memberId = stdMember.getId();
		String newNickname = "newNickname";
		MemberUpdateNicknameRequest nicknameRequest = MemberUpdateNicknameRequest.from(newNickname);
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(stdMember));

		// when - 새로운 닉네임으로 update 시도
		memberService.updateNickname(memberId, nicknameRequest);

		// then
		// nickname이 변경되었는지 확인
		assertThat(stdMember.getNickname()).isEqualTo(newNickname);

	}

	/**
	 * <h2>[예외]  중복 닉네임으로 닉네임 수정 요청 테스트</h2>
	 * <p>결과: updateNickname()가 호출되지 않고, 닉네임이 변경되지 않음. Exception 발생 </p>
	 * <p>검증 방식: existsByNickname() 호출 횟수, findById() 호출 횟수, 닉네임 변경 여부, 예외 확인</p>
	 * @see MemberService#updateNickname(Long, MemberUpdateNicknameRequest)
	 */
	@Test
	@DisplayName("[예외] 중복 닉네임으로 닉네임 수정 요청 - updateNickname")
	void updateNicknameThrows() {

		// given - 이미 존재하는 닉네임
		Long memberId = stdMember.getId();
		String stdNickname = stdMember.getNickname();
		String newNickname = "newNickname";
		MemberUpdateNicknameRequest nicknameRequest = MemberUpdateNicknameRequest.from(newNickname);
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(stdMember));
		when(memberRepository.existsByNickname(newNickname)).thenReturn(true);

		// when - 이미 존재하는 닉네임으로 update 시도

		// then
		// 중복 닉네임인 경우 발생하는 BaseException 및 메시지 확인
		assertThatThrownBy(() -> memberService.updateNickname(memberId, nicknameRequest))
			.isInstanceOf(BaseException.class)
			.hasMessage(String.format("이미 사용 중인 닉네임입니다. nickname=%s", newNickname));
		// memberRepository의 existsByNickname()가 호출되는지 확인
		verify(memberRepository, times(1)).existsByNickname(newNickname);
		// nickname이 변경되었는지 확인 - 예외이므로 바뀌지 않고, 기존 그대로
		assertThat(stdMember.getNickname()).isEqualTo(stdNickname);

	}

	/**
	 * <h2>[정상] memberId로 MiniProfile 반환 테스트</h2>
	 * <p>결과: MemberMiniProfileResponse 반환</p>
	 * <p>검증 방식: repository 호출 횟수, 반환 타입의 필드 직접 비교</p>
	 * @see MemberService#getMiniProfileById(Long)
	 */ // TODO 반환 DTO의 from 등에 의존하는 방식 개선
	@Test
	@DisplayName("[정상] memberId로 MiniProfile 반환 - getMiniProfileById")
	void getMiniProfileById() {

		// given - Repository에서 findById()가 호출되면 stdMember를 반환하도록 설정
		when(memberRepository.findById(stdMember.getId())).thenReturn(Optional.of(stdMember));

		// when
		// MemberService의 getMiniProfileById()가 실제 호출됐을 때 actualResponse 반환
		MemberSimpleResponse actualResponse = memberService.getMiniProfileById(stdMember.getId());

		// then
		// actualResponse의 필드와 기댓값 비교
		assertThat(actualResponse.getMemberId()).isEqualTo(1);
		assertThat(actualResponse.getNickname()).isEqualTo("stdNickname");
		assertThat(actualResponse.getProfileImgUrl()).isEqualTo("imageUrl");


	}

}