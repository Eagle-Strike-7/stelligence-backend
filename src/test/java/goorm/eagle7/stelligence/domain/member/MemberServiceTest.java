package goorm.eagle7.stelligence.domain.member;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
import goorm.eagle7.stelligence.domain.badges.model.Badge;
import goorm.eagle7.stelligence.domain.member.dto.MemberBadgesListResponse;
import goorm.eagle7.stelligence.domain.member.dto.MemberDetailResponse;
import goorm.eagle7.stelligence.domain.member.dto.MemberSimpleResponse;
import goorm.eagle7.stelligence.domain.member.dto.MemberUpdateNicknameRequest;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.member.model.SocialType;
import goorm.eagle7.stelligence.domain.withdrawnmember.WithdrawnMemberRepository;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@Mock
	private CookieUtils cookieUtils; // doNothing()을 위해 필요, TODO 따로 test
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private WithdrawnMemberRepository withdrawnMemberRepository;

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

		// given - Repository - findByIdAndActiveTrue 시 null 반환
		Long stdMemberId = stdMember.getId();
		when(memberRepository.findByIdAndActiveTrue(stdMemberId)).thenReturn(Optional.empty());

		// when

		// then
		// 존재하지 않는 멤버를 조회했을 때 발생하는 BaseException 및 메시지 확인
		assertThatThrownBy(() -> memberService.getProfileById(stdMemberId))
			.isInstanceOf(BaseException.class)
			.hasMessage("존재하지 않는 회원입니다. MemberId= 1"); // 서식 문자 사용에 의존하지 않기 위해 하드 코딩, 1L은 1로 변환됨.

		// Repository의 findByIdAndActiveTrue()가 호출됐는지 확인 - 1번
		verify(memberRepository, times(1)).findByIdAndActiveTrue(stdMemberId);

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

		// given - Repository에서 findByIdAndActiveTrue()가 호출되면 stdMember를 반환하도록 설정
		when(memberRepository.findByIdAndActiveTrue(stdMember.getId())).thenReturn(Optional.of(stdMember));

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
		when(memberRepository.findByIdAndActiveTrue(memberId)).thenReturn(Optional.of(stdMember));

		// 탈퇴한 회원 Table로 따로 저장 - 추후 배치
		doNothing().when(withdrawnMemberRepository).insertWithdrawnMember(stdMember);

		// when
		memberService.delete(memberId);

		// then
		// soft delete 진행 확인

		// member active false로 변경 확인
		assertThat(stdMember.isActive()).isFalse();

		// 탈퇴한 회원 nickname update 확인
		String nickname = "탈퇴한 회원NeutronStar"+ memberId;
		assertThat(stdMember.getNickname()).isEqualTo(nickname);

		// 탈퇴한 회원 전부 null인지 확인
		assertThat(stdMember.getName()).isNull();
		assertThat(stdMember.getEmail()).isNull();
		assertThat(stdMember.getImageUrl()).isNull();
		assertThat(stdMember.getSocialId()).isNull();
		assertThat(stdMember.getSocialType()).isEqualTo(SocialType.WHITDRAWN);
		assertThat(stdMember.getRefreshToken()).isNull();
		assertThat(stdMember.getContributes()).isZero();
		assertThat(stdMember.getBadges()).isEmpty();

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
		when(memberRepository.findByIdAndActiveTrue(nonExistentMemberId)).thenReturn(Optional.empty());

		// when


		// then - 존재하지 않는 memberId여도 deleteById()가 호출되고, 아무 일도 일어나지 않음.
		assertThatThrownBy(() -> memberService.delete(nonExistentMemberId))
			.isInstanceOf(BaseException.class)
			.hasMessage("존재하지 않는 회원입니다. MemberId= 999"); // 서식 문자 사용에 의존하지 않기 위해 하드 코딩, 999L은 999로 변환됨.

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
		when(memberRepository.findByIdAndActiveTrue(memberId)).thenReturn(Optional.of(stdMember));

		// when - 새로운 닉네임으로 update 시도
		memberService.updateNickname(memberId, nicknameRequest);

		// then
		// nickname이 변경되었는지 확인
		assertThat(stdMember.getNickname()).isEqualTo(newNickname);

	}

	/**
	 * <h2>[예외]  중복 닉네임으로 닉네임 수정 요청 테스트</h2>
	 * <p>결과: updateNickname()가 호출되지 않고, 닉네임이 변경되지 않음. Exception 발생 </p>
	 * <p>검증 방식: existsByNickname() 호출 횟수, findByIdAndActiveTrue() 호출 횟수, 닉네임 변경 여부, 예외 확인</p>
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
		when(memberRepository.findByIdAndActiveTrue(memberId)).thenReturn(Optional.of(stdMember));
		when(memberRepository.existsByNicknameAndActiveTrue(newNickname)).thenReturn(true);

		// when - 이미 존재하는 닉네임으로 update 시도

		// then
		// 중복 닉네임인 경우 발생하는 BaseException 및 메시지 확인
		assertThatThrownBy(() -> memberService.updateNickname(memberId, nicknameRequest))
			.isInstanceOf(BaseException.class)
			.hasMessage(String.format("이미 사용 중인 닉네임입니다. nickname=%s", newNickname));
		// memberRepository의 existsByNickname()가 호출되는지 확인
		verify(memberRepository, times(1)).existsByNicknameAndActiveTrue(newNickname);
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

		// given - Repository에서 findByIdAndActiveTrue()가 호출되면 stdMember를 반환하도록 설정
		when(memberRepository.findByIdAndActiveTrue(stdMember.getId())).thenReturn(Optional.of(stdMember));

		// when
		// MemberService의 getMiniProfileById()가 실제 호출됐을 때 actualResponse 반환
		MemberSimpleResponse actualResponse = memberService.getMiniProfileById(stdMember.getId());

		// then
		// actualResponse의 필드와 기댓값 비교
		assertThat(actualResponse.getMemberId()).isEqualTo(1);
		assertThat(actualResponse.getNickname()).isEqualTo("stdNickname");
		assertThat(actualResponse.getProfileImgUrl()).isEqualTo("imageUrl");

	}

	/**
	 * <h2>[정상] memberId로 배지 조회 테스트</h2>
	 * <p>결과: MemberBadgesListResponse 반환</p>
	 * <p>검증 방식: stdBadges를 가진 Member를 반환하도록 하고, service의 결과가 해당 stdBadges의 크기와 같은지 비교</p>
	 */
	@Test
	@DisplayName("[정상] 배지 조회 - getBadgesById")
	void getBadgesById() {

		// given
		// 테스트용 배지 Set
		Set<Badge> stdBadges = new HashSet<>();
		stdBadges.add(Badge.MERCURY);
		stdBadges.add(Badge.VENUS);
		stdBadges.add(Badge.NEPTUNE);
		stdBadges.add(Badge.ANDROMEDA);
		stdBadges.add(Badge.GALAXY);

		// stdMember를 Mock 객체로 변경, stdBadges를 반환하도록 설정
		Member stdMember = mock(Member.class);
		when(stdMember.getId()).thenReturn(1L);
		when(stdMember.getBadges()).thenReturn(stdBadges);
		// Repository에서 findByIdAndActiveTrue()가 호출되면 stdMember를 반환하도록 설정
		when(memberRepository.findByIdAndActiveTrue(stdMember.getId())).thenReturn(Optional.of(stdMember));

		// when
		MemberBadgesListResponse badgesListResponse = memberService.getBadgesById(stdMember.getId());

		// then
		assertThat(badgesListResponse.getBadges()).hasSize(5);

	}

}