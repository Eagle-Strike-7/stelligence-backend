package goorm.eagle7.stelligence.domain.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.common.auth.memberinfo.Auth;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
import goorm.eagle7.stelligence.common.login.CookieUtils;
import goorm.eagle7.stelligence.domain.member.dto.MemberSimpleResponse;
import goorm.eagle7.stelligence.domain.member.dto.MemberDetailResponse;
import goorm.eagle7.stelligence.domain.member.dto.MemberUpdateNicknameRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Member API", description = "회원 정보를 조회하고, 수정하고, 삭제하는 API를 제공합니다.")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

	@Value("${http.cookie.accessToken.name}")
	private String accessCookieName;
	@Value("${http.cookie.refreshToken.name}")
	private String refreshCookieName;

	private final MemberService memberService;


	@Operation(summary = "간단한 회원 정보 조회", description = "페이지마다 우측 상단에서 보이는 닉네임과 프로필url를 조회합니다")
	@ApiResponse(
		responseCode = "200",
		description = "간단한 회원 정보 조회 성공",
		useReturnTypeSchema = true
	)
	@GetMapping("/members/me/mini-profile")
	public ResponseTemplate<MemberSimpleResponse> findMiniProfileFromMember(@Auth MemberInfo memberInfo) {
		MemberSimpleResponse memberSimpleResponse = memberService.getMiniProfileById(memberInfo.getId());
		return ResponseTemplate.ok(memberSimpleResponse);
	}

	@Operation(summary = "회원 정보 조회", description = "회원 정보를 조회합니다")
	@ApiResponse(
		responseCode = "200",
		description = "회원 정보 조회 성공",
		useReturnTypeSchema = true
	)
	@GetMapping("/members/me")
	public ResponseTemplate<MemberDetailResponse> findMember(@Auth MemberInfo memberInfo) {
		MemberDetailResponse memberDetailResponse = memberService.getProfileById(memberInfo.getId());
		return ResponseTemplate.ok(memberDetailResponse);
	}

	@Operation(summary = "회원 탈퇴", description = "회원을 삭제합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "회원 탈퇴 성공",
		useReturnTypeSchema = true
	)
	@DeleteMapping("/members/me")
	public ResponseTemplate<Void> deleteMember(@Auth MemberInfo memberInfo,
		HttpServletRequest request, HttpServletResponse response) {

		memberService.delete(memberInfo.getId());

		// 탈퇴 시 쿠키 제거
		CookieUtils.deleteCookie(request, response, accessCookieName);
		CookieUtils.deleteCookie(request, response, refreshCookieName);

		// SecurityContext 초기화
		// SecurityContextHolder.clearContext();

		return ResponseTemplate.ok();
	}

	@Operation(summary = "회원 가입 이후 닉네임 수정", description = "회원 가입 이후 닉네임을 수정합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "닉네임 수정 성공",
		useReturnTypeSchema = true
	)
	@PutMapping("/members/me/nickname")
	public ResponseTemplate<Void> updateNickname(
		@Auth MemberInfo memberInfo,
		@Parameter(description = "변경할 닉네임", example = "은하수")
		@RequestBody MemberUpdateNicknameRequest memberUpdateNicknameRequest) {
		memberService.updateNickname(memberInfo.getId(), memberUpdateNicknameRequest);
		return ResponseTemplate.ok();
	}

	@Operation(summary = "회원의 뱃지 조회", description = "회원의 뱃지를 조회합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "회원의 뱃지 조회 성공",
		useReturnTypeSchema = true
	)
	@GetMapping("/members/me/badges")
	public ResponseTemplate<List<MemberBadgesResponse>> findMemberBadges(@Auth MemberInfo memberInfo) {
		List<MemberBadgesResponse> badgesById = memberService.getBadgesById(memberInfo.getId());
		return ResponseTemplate.ok(badgesById);
	}

}
