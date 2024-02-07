package goorm.eagle7.stelligence.common.dev;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.common.dev.dto.DevLoginRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DevLoginController {

	private final DevLoginService devLoginService;

	/**
	 * 로그인 혹은 회원 가입
	 * @param devLoginRequest 닉네임
	 * @return socialType 토큰
	 */
	@PostMapping("/login")
	public ResponseTemplate<Void> login(@RequestBody DevLoginRequest devLoginRequest) {

		// 로그인 혹은 회원 가입
		devLoginService.devLogin(devLoginRequest);

		return ResponseTemplate.ok();
	}

}
