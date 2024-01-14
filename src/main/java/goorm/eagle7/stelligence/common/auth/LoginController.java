package goorm.eagle7.stelligence.common.auth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import goorm.eagle7.stelligence.api.ApiResponse;
import goorm.eagle7.stelligence.common.auth.dto.LoginRequest;
import goorm.eagle7.stelligence.common.auth.dto.LoginTokensResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api") // TODO /auth
public class LoginController {

	private final LoginService loginService;

	@PostMapping("/login")
	public ApiResponse<LoginTokensResponse> login(@RequestBody LoginRequest loginRequest) {

		LoginTokensResponse loginTokensResponse = loginService.login(loginRequest);

		return ApiResponse.ok(loginTokensResponse);
	}

}
