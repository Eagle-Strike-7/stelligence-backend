package goorm.eagle7.stelligence.common.login;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.common.login.dto.DevLoginRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DevLoginController {

	private final LoginService loginService;

	/**
	 * <h2>로그인 혹은 회원 가입</h2>
	 * <p>- 닉네임으로 로그인 혹은 회원 가입</p>
	 * <p>- 로그인 성공 시, accessToken, refreshToken을 쿠키에 저장</p>
	 * @param devLoginRequest 닉네임
	 *
	 */
	@PostMapping("/login")
	public ResponseTemplate<Void> login(@RequestBody DevLoginRequest devLoginRequest) {
		// 로그인 혹은 회원 가입
		loginService.login(devLoginRequest);
		return ResponseTemplate.ok();

	}

}
