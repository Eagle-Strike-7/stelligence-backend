package goorm.eagle7.stelligence.common.login;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.common.auth.memberinfo.Auth;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfoContextHolder;
import goorm.eagle7.stelligence.common.login.dto.DevLoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DevLoginController {

	private final CookieUtils cookieUtils;
	private final LoginService loginService;

	@Operation(summary = "로그인 - 개발용", description = "로그인 시도합니다. 회원이 없으면 회원 가입 후 로그인합니다. 로그인이란, 쿠키를 발급하는 과정을 의미합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "로그 성공",
		useReturnTypeSchema = true
	)
	@PostMapping("/login")
	public ResponseTemplate<Void> login(@RequestBody DevLoginRequest devLoginRequest) {
		// 로그인 혹은 회원 가입
		loginService.login(devLoginRequest);
		return ResponseTemplate.ok();

	}

	@Operation(summary = "로그아웃", description = "로그아웃합니다. 로그인 상태가 아니어도 호출 가능합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "회원 정보 조회 성공",
		useReturnTypeSchema = true
	)
	@PostMapping("/logout")
	public ResponseTemplate<Void> devLogout(@Auth MemberInfo memberInfo) {

		loginService.devLogout(memberInfo);

		// 쿠키 삭제
		cookieUtils.deleteCookieBy(CookieType.ACCESS_TOKEN);
		cookieUtils.deleteCookieBy(CookieType.REFRESH_TOKEN);

		// ThreadLocal 초기화
		MemberInfoContextHolder.clear();

		return ResponseTemplate.ok();
	}

}
