package goorm.eagle7.stelligence.common.login;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.common.login.dto.DevLoginRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DevLoginController {

	private final LoginService loginService;

	/**
	 * 로그인 혹은 회원 가입
	 * @param devLoginRequest 닉네임
	 * @param response response cookie에 token 저장
	 * @return socialType 토큰
	 */
	@PostMapping("/login")
	public ResponseTemplate<Void> login(@RequestBody DevLoginRequest devLoginRequest,
		HttpServletResponse response) {

		// 로그인 혹은 회원 가입
		loginService.devLogin(response, devLoginRequest);

		return ResponseTemplate.ok();
	}
}
