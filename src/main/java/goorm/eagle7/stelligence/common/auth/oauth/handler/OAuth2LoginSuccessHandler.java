package goorm.eagle7.stelligence.common.auth.oauth.handler;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.common.auth.filter.ResponseTemplateUtils;
import goorm.eagle7.stelligence.common.auth.oauth.CustomOAuth2User;
import goorm.eagle7.stelligence.common.login.LoginService;
import goorm.eagle7.stelligence.common.login.OAuth2Request;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final LoginService loginService;

	/**
	 * 로그인 성공 시 호출되는 메서드
	 *
	 * 1. 로그인 성공 시, loginService에서 회원 가입 혹은 로그인 후 토큰 쿠키 발행 후 response에 추가
	 * 2. 성공 응답 200 전송
	 *
	 * OAuth2User가 아닌 경우 InternalAuthenticationServiceException 발생
	 * 	-> Spring Security의 예외 처리 체인을 따라 처리
	 *
	 * @param authentication 인증 객체, Service에서 반환한 CustomOAuth2User 저장되어 있음.
	 *
	 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) {

		if (!(authentication.getPrincipal() instanceof CustomOAuth2User customOAuth2User)) {
			// OAuth2User가 아닌 경우 에러, Spring Security의 예외 처리 체인을 따라 401 처리됨.
			throw new InternalAuthenticationServiceException("Unsupported user type");
		}

		// loginServie를 이용할 Dto 생성
		OAuth2Request oAuth2Request = OAuth2Request.of(
			customOAuth2User.getName(),
			customOAuth2User.getNickname(),
			customOAuth2User.getEmail(),
			customOAuth2User.getImageUrl(),
			customOAuth2User.getSocialId(),
			customOAuth2User.getSocialType()
		);

		/**
		 * 사용자를 DB에 저장 및 토큰 발행
		 * -> socialId로 중복 확인, 없다면 -> signUpService.signUp() -> MemberRepository.save() Member 생성
		 * -> memberId로 토큰 생성 및 DB 저장, response에 토큰 쿠키 추가
		 */
		loginService.login(response, oAuth2Request);

		try {
			response.setHeader("custom-header", "success");
			RequestDispatcher dispatcher = request.getRequestDispatcher("http://3.39.192.156");
			dispatcher.forward(request, response);
		} catch (ServletException | IOException e) {
			throw new AccessDeniedException("Redirect failed");
		}

		// ResponseTemplate<Void> responseTemplate = ResponseTemplate.ok();
		// ResponseTemplateUtils.toSuccessResponse(response, responseTemplate);

	}
}
