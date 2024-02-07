package goorm.eagle7.stelligence.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.filter.CorsFilter;

import goorm.eagle7.stelligence.common.auth.filter.AuthExceptionHandlerFilter;
import goorm.eagle7.stelligence.common.auth.filter.AuthFilter;
import goorm.eagle7.stelligence.common.auth.filter.handler.CustomAccessDeniedHandler;
import goorm.eagle7.stelligence.common.auth.filter.handler.CustomAuthenticationEntryPoint;
import goorm.eagle7.stelligence.common.auth.filter.pathmatch.CustomRequestMatcher;
import goorm.eagle7.stelligence.common.auth.oauth.handler.OAuth2LoginFailureHandler;
import goorm.eagle7.stelligence.common.auth.oauth.handler.OAuth2LoginSuccessHandler;
import goorm.eagle7.stelligence.common.auth.oauth.handler.OAuth2LogoutCustomHandler;
import goorm.eagle7.stelligence.common.auth.oauth.handler.OAuth2LogoutSuccessHandler;
import goorm.eagle7.stelligence.common.auth.oauth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	@Value("${jwt.accessToken.name}")
	private String accessTokenCookieName;

	@Value("${jwt.refreshToken.name}")
	private String refreshTokenCookieName;

	private final AuthFilter authFilter;
	private final AuthExceptionHandlerFilter authExceptionHandlerFilter;
	private final CorsFilter corsFilter;
	private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
	private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
	private final OAuth2LogoutCustomHandler oAuth2LogoutCustomHandler;
	private final OAuth2LogoutSuccessHandler oAuth2LogoutSuccessHandler;
	private final CustomAccessDeniedHandler customAccessDeniedHandler;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final CustomRequestMatcher customRequestMatcher;

	/**
	 * .ignoring():
	 * 		- 인증 및 권한 검사 없이 접근 가능해 Spring Security의 보안 처리를 완전히 우회함.
	 * 		- 주로 정적 리소스에 사용됨.
	 * .permitAll():
	 * 		- 모든 사용자의 접근을 허용
	 * 		- CSRF 보호, XSS 방지 등 Spring Security의 다른 보안 메커니즘은 여전히 적용됨.
	 * 		- 주로 특정 API 엔드포인트나 페이지에 사용됨.
	 * 		- 보안 검사는 필요하지만 인증은 필요하지 않은 경우에 적합.
	 * @return WebSecurityCustomizer : WebSecurityCustomizer
	 */

	// TODO error 페이지는 인증 없어도 접근 가능하게 해야함. webSecurityCustomizer()에서 어떤 처리?
	// TODO 인증과 없는 페이지 에러 처리 중 어떤 게 먼저인 게 적절할지.
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring()
			.requestMatchers( "/css/**", "/images/**", "/js/**", "/favicon.ico", "/fonts/**", " /assets/**","/favicon.ico",
				"/error",
				"/swagger-ui/**",
				"/swagger-resources/**",
				"/v3/api-docs/**");
	}

	/**
	 * 1. csrf -> token 기반으로 비활성화
	 * 2. httpBasic -> form 기반 로그인 비활성화
	 * 3. sessionManagement -> 세션 비활성화 for token
	 * 4. authorizeHttpRequests -> 인가된 사용자만 접근 가능하도록 설정
	 * 		- permitAll -> 인증되지 않은 사용자도 접근 가능
	 * 			- /api/login -> test nickname 가입 용도
	 * 			- /oauth2/** -> oauth2 로그인 요청
	 * 			- /login/oauth2/code/** -> oauth2 로그인 성공 후 redirect uri
	 * 		- anyRequest().authenticated() -> 인증된 사용자만 접근 가능
	 * 			- /api/** -> 인가된 사용자만 접근 가능
	 * 5. addFilterBefore -> corsFilter, authFilter 추가
	 * 		- corsFilter -> cors 설정
	 * 		- authFilter -> 토큰 검증, Authentication 저장, 인증되지 않으면 throw Error			- LogoutFilter 전에 해야 logout 시에도 Authentication 사용 가능
	 * 6. exceptionHandling ->
	 * 		- AuthenticationEntryPoint 인증되지 않은 사용자가 접근하면 401
	 * 			- InvalidCookieException, JwtException, AuthenticationException
	 * 		- AccessDeniedException 인가되지 않은 사용자가 접근하면 403
	 * 7. oauth2Login ->
	 * 		- userInfoEndpoint -> oauth2 로그인 성공 후 서비스 로직,
	 * 			- OAuth2UserService: 각 OAuth2 제공자에 맞춰 oauth2User 정보를 가져오는 서비스
	 * 			- OAuth2User는 Authentication 객체의 일부로 SecurityContextHolder에 저장
	 * 		- successHandler -> oauth2 로그인 성공 후 서비스 로직
	 * 			- 로그인 성공 시, 회원 가입 혹은 로그인 후 토큰 쿠키 발행해 response에 추가
	 *	 	 	- 사용자 인증 과정이 완료 이후 임시 인증 관련 데이터를 세션에서 제거
	 * 		- failureHandler -> oauth2 로그인 실패 후 서비스 로직
	 * 			- 로그인 실패 시, error message 발행해 response에 추가
	 * 8. logout -> /api/logout 요청 시 로그아웃
	 * 		- LogoutHandler에서 DB의 refreshToken 삭제
	 * 		- cookie에서 accessToken, refreshToken 삭제
	 * 		- Security의 Authentication 삭제
	 * - OAuth2 로그인
	 * 		- 클라이언트에서 요청하는 링크: http://{localhost:8080}/oauth2/authorization/{kakao}
	 * 		- 해당 링크로 로그인 요청을 보내면 security가 인가 코드를 받아서 accessToken 발급 후 userInfoEndpoint 이후로 진행
	 * @param http : HttpSecurity
	 * @return SecurityFilterChain
	 * @throws Exception : Exception
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
			.csrf(AbstractHttpConfigurer::disable) // csrf -> token 기반으로 비활성화
			.formLogin(AbstractHttpConfigurer::disable) // form 기반 로그인 비활성화
			.httpBasic(
				AbstractHttpConfigurer::disable) // BasicAuthenticationFilter 사용 X (대신 JWT 토큰, OAuth 방식 사용), formLoginFilter 사용 X(사용자 정의 로그인 페이지 이용)
			.sessionManagement(sessionManagement -> sessionManagement
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			) // for token
			.authorizeHttpRequests(request -> request
				.requestMatchers(customRequestMatcher)
				.permitAll()
				.requestMatchers("/api/**").hasRole("USER")
				.anyRequest().authenticated())

			.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(authFilter,
				LogoutFilter.class) // 토큰 검증, Authentication 저장, 인증되지 않으면 throw Error // LogoutFilter 전에 해야 logout 시에도 Authentication 사용 가능 TODO permitall 이용할 수 있도록 수정
			.addFilterBefore(authExceptionHandlerFilter, AuthFilter.class)
			.exceptionHandling(exceptionHandling -> exceptionHandling
				.accessDeniedHandler(customAccessDeniedHandler)// 인가되지 않은 사용자가 접근하면 403 -> 400 통일
				.authenticationEntryPoint(customAuthenticationEntryPoint) // 인증되지 않은 사용자가 접근하면 401
			)
			.oauth2Login(oauth2 -> oauth2
				.userInfoEndpoint(userInfo -> userInfo // userInfo == oauth2User // oauth2 로그인 성공 후 서비스 로직
					.userService(customOAuth2UserService)
				)
				.successHandler(oAuth2LoginSuccessHandler)
				.failureHandler(oAuth2LoginFailureHandler)
			)
			// 서비스 내 로그아웃만 관리, 소셜 버튼 누르면 로그인 창 뜨지 않고 해당 서비스에 바로 재로그인 가능
			// session 무효화 X - 다른 데서 이용할 수도 있음. 불필요한 일 X.
			.logout(
				logout -> logout
					.logoutRequestMatcher(
						request -> request.getServletPath().equals("/api/logout")
					) // 로그아웃 요청 url 설정 (default: /logout)
					.addLogoutHandler(oAuth2LogoutCustomHandler) // 로그아웃 시 refreshToken 삭제 // handler 순서 중요함.
					.deleteCookies(accessTokenCookieName, refreshTokenCookieName) // cookie 삭제, (JSESSIONID은 톰캣 사용)
					.clearAuthentication(true) // 순서 상관 X
					.logoutSuccessHandler(oAuth2LogoutSuccessHandler)
					.permitAll() // 세션이 만료된 상태 등에서도 로그아웃 가능. (사용자 경험 위해)
			);

		return http.build();
	}
}
