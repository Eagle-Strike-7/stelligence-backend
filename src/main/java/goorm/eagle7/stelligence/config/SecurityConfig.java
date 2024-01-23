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
import org.springframework.web.filter.CorsFilter;

import goorm.eagle7.stelligence.common.auth.filter.AuthFilter;
import goorm.eagle7.stelligence.common.auth.filter.ResourceAntPathMatcher;
import goorm.eagle7.stelligence.common.auth.oauth.handler.OAuth2LogoutCustomHandler;
import goorm.eagle7.stelligence.common.auth.oauth.handler.OAuth2LogoutSuccessHandler;
import goorm.eagle7.stelligence.common.auth.oauth.service.CustomOAuth2UserService;
import goorm.eagle7.stelligence.common.auth.oauth.handler.OAuth2LoginFailureHandler;
import goorm.eagle7.stelligence.common.auth.oauth.handler.OAuth2LoginSuccessHandler;
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
	private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
	private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
	private final OAuth2LogoutCustomHandler oAuth2LogoutCustomHandler;
	private final OAuth2LogoutSuccessHandler oAuth2LogoutSuccessHandler;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final CorsFilter corsFilter;
	private final ResourceAntPathMatcher resourceAntPathMatcher;

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring().requestMatchers("/static/**");
	}

	// TODO 권한 없으면 EXCEPTION handler, Exception 설정.
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
			.csrf(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable) // BasicAuthenticationFilter 사용 X (일회성으로 페이지 불러오는 필터) // formLoginFilter 사용 X
			.sessionManagement(sessionManagement -> sessionManagement
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			) // for token
			.authorizeHttpRequests(request -> request
				.requestMatchers("/", "/css/**", "/images/**", "/js/**", "/favicon.ico", "/index.html",
					"/login.html", "logout.html", "/failure.html", "/api/login", "/login", "/oauth2/**", "/login/oauth2/code/**") // "/static/**" 이건 작동 X
				.permitAll() // login, logout, static 파일은 누구나 접근 가능
				.requestMatchers("/api/**").hasRole("USER")
				.anyRequest().authenticated())

			.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(authFilter,
				UsernamePasswordAuthenticationFilter.class) // 토큰 검증하고, Member 정보를 holder에 담아 넘겨 줌.
			// .exceptionHandling(exceptionHandling -> exceptionHandling
			// 	.accessDeniedHandler(jwtAccessDeniedHandler)
			// 	.authenticationEntryPoint(jwtAuthenticationEntryPoint)
			// )
			.oauth2Login(oauth2 -> oauth2
				// .loginPage("/login.html") // 설정 안 하면 무조건 /login으로 이동, 개발에서만 사용. production에서는 설정 X
				.userInfoEndpoint(userInfo -> userInfo // userInfo == oauth2User // oauth2 로그인 성공 후 가져올 때의 설정들
					.userService(customOAuth2UserService)
				)
				.successHandler(oAuth2LoginSuccessHandler)
				.failureHandler(oAuth2LoginFailureHandler)
			)
			// security가 인가 코드를 아예 받아서 accesstoken까지 주기 때문에 프론트는  oauth/authorize로 로그인 요청을 보내기만 함.
			// http://localhost:8080/oauth2/authorization/kakao -> 해당 링크로 이동
			.logout( // 서비스 내 로그아웃만 관리, 소셜 버튼 누르면 로그인 창 뜨지 않고 해당 서비스에 바로 재로그인 가능
				logout -> logout
					.logoutUrl("/api/logout")
				.addLogoutHandler(oAuth2LogoutCustomHandler) // TODO refreshToken 만료 처리
				// .invalidateHttpSession(true) // session 무효화 - 다른 데서 이용할 수도 있으니 설정 X
				.deleteCookies(accessTokenCookieName, refreshTokenCookieName) // cookie 삭제, 톰캣 - JSESSIONID
				.logoutSuccessHandler(oAuth2LogoutSuccessHandler)
				.clearAuthentication(true) // SecurityContextHolder.clearContext();
				.logoutSuccessUrl("/logout.html") // 여기보다 handler 설정이 더 우선, handler 설정 시 이 url은 무시됨. 개발에서만 사용
				.permitAll()
			)
		;

		return http.build();

	}

}
