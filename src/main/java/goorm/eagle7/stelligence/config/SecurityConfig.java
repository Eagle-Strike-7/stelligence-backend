package goorm.eagle7.stelligence.config;

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
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.CorsFilter;

import goorm.eagle7.stelligence.common.auth.filter.AuthFilter;
import goorm.eagle7.stelligence.common.auth.filter.handler.CustomAccessDeniedHandler;
import goorm.eagle7.stelligence.common.auth.filter.handler.CustomAuthenticationEntryPoint;
import goorm.eagle7.stelligence.common.auth.filter.pathmatch.PermitAllRequestMatcher;
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

	private final CorsFilter corsFilter;
	private final AuthFilter authFilter;
	private final PermitAllRequestMatcher permitAllRequestMatcher;
	private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
	private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
	private final OAuth2LogoutCustomHandler oAuth2LogoutCustomHandler;
	private final OAuth2LogoutSuccessHandler oAuth2LogoutSuccessHandler;
	private final CustomAccessDeniedHandler customAccessDeniedHandler;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	private final CustomOAuth2UserService customOAuth2UserService;

	private static final String[] STATIC_PATHS = {
		"/css/**", "/images/**", "/js/**", "/favicon.ico", "/fonts/**", " /assets/**", "/favicon.ico", "/error"
	};

	/**
	 * <h2>WebSecurityCustomizer</h2>
	 * <p>- .ignoring(): Spring Security 필터 통과.(인증 및 권한 검사 없이 접근 가능)</p>
	 * <p>- .permitAll(): 모든 사용자의 접근 허용, CSRF 보호, XSS 방지 등 Spring Security의 다른 보안 메커니즘은 여전히 적용됨</p>
	 * @return WebSecurityCustomizer : WebSecurityCustomizer
	 */
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring()
			.requestMatchers(STATIC_PATHS);
	}

	/**
	 * <h2>SecurityFilterChain</h2>
	 * <p>- filterChain: security 설정</p>
	 * <p>- 소셜 로그인 요청 링크: /oauth2/authorization/{kakao}</p>
	 * @param http : HttpSecurity
	 * @return SecurityFilterChain
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
			/* csrf, formLogin, httpBasic, sessionManagement 설정 */
			.csrf(AbstractHttpConfigurer::disable) // csrf -> token 기반으로 비활성화
			.formLogin(AbstractHttpConfigurer::disable) // form 기반 로그인 비활성화
			.httpBasic(AbstractHttpConfigurer::disable) // 사용자 정의 로그인 페이지 이용
			.sessionManagement(sessionManagement -> sessionManagement
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			) // token 사용으로 session 비활성화

			/* authorizeHttpRequests: 권한에 따른 접근 경로 설정 */
			// 선언하는 순서대로 검사, 순서 중요.
			.authorizeHttpRequests(request -> request
				// preflight 요청 허용
				.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
				// permitAllRequestMatcher: permitAll 경로 설정
				.requestMatchers(permitAllRequestMatcher).permitAll()
				// /api/** 경로에 대해 USER 권한을 가진 사용자만 접근 가능
				.requestMatchers("/api/**").hasRole("USER")
				// 나머지 경로에 대해 인증된 사용자만 접근 가능
				.anyRequest().authenticated()
			)

			/* addFilter: cors, auth(토큰) 추가 */
			.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
			// LogoutFilter 전에 해야 logout 시에도 Authentication 사용 가능
			// 토큰 검증, Authentication 저장
			.addFilterBefore(authFilter, LogoutFilter.class)

			/* exceptionHandling: 인증되지 않은 사용자가 접근하면 401, 인가되지 않은 사용자가 접근하면 403 */
			.exceptionHandling(exceptionHandling -> exceptionHandling
				// 인증되지 않은 사용자가 접근하면 401
				.authenticationEntryPoint(customAuthenticationEntryPoint)
				// 인가되지 않은 사용자가 접근하면 403
				.accessDeniedHandler(customAccessDeniedHandler)
			)

			/* oauth2Login: oauth2 로그인 서비스 로직 */
			.oauth2Login(oauth2 -> oauth2
				.userInfoEndpoint(userInfo -> userInfo
					// userInfo == oauth2User
					// oauth2 로그인 시 서비스 로직
					.userService(customOAuth2UserService)
				)
				.successHandler(oAuth2LoginSuccessHandler)
				.failureHandler(oAuth2LoginFailureHandler)
			)

			/* logout: 로그아웃 서비스 로직 */
			.logout(
				logout -> logout
					// 로그아웃 요청 uri 설정 (default: /logout)
					.logoutRequestMatcher(
						request -> request.getServletPath().equals("/api/logout"))
					.addLogoutHandler(oAuth2LogoutCustomHandler) // 로그아웃 시 refreshToken, 쿠키 삭제
					.clearAuthentication(true)
					.logoutSuccessHandler(oAuth2LogoutSuccessHandler) // 로그아웃 성공 시, 200 응답
					.permitAll() // 로그아웃 uri에서 로그아웃 가능.
				// session 무효화 X - 다른 데서 이용할 수도 있음. 불필요한 일 X.
			);

		return http.build();
	}
}
