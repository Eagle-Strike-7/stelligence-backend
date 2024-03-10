package goorm.eagle7.stelligence.common.auth.oauth.service;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.common.auth.oauth.CustomOAuth2User;
import goorm.eagle7.stelligence.domain.member.model.SocialType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	/**
	 * 기능: OAuth2UserRequest에서 소셜 로그인 제공자를 추출하고, 원하는 정보를 얻어 CustomOAuth2User에 저장 후 반환.
	 * 	- oAuth2User: 각 소셜 로그인 서비스에서 제공하는 정보가 담긴 DefaultOAuth2User 객체
	 * 		- 해당 OAuth2UserService - loadUser 메서드를 통해 반환된 OAuth2User는 Authentication 객체의 일부(principal)로 SecurityContextHolder에 자동 저장됨.
	 *
	 * @param userRequest OAuth2UserRequest
	 *                    - clientRegistration: socialType
	 *                    - accessToken: social 제공자에게 user의 정보를 받을 때 서버를 인증하는 토큰
	 * @return CustomOAuth2User(OAuth2User 상속)(SecurityContextHolder에 자동 저장됨)
	 * @throws OAuth2AuthenticationException
	 * 		-> 로그인 실패로 간주, SecurityContextHolder에 저장되지 않고, OAuth2LoginFailureHandler에서 처리.
	 */
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		OAuth2User oAuth2User = super.loadUser(userRequest);

		// 소셜 타입 추출
		SocialType socialType = SocialType.valueOf(
			userRequest.getClientRegistration().getRegistrationId().toUpperCase());

		// attributes에서 각 socialType에 맞게 socialId, socialType, name, nickname, imageUrl, email 확인 후 저장
		Map<String, Object> attributes = oAuth2User.getAttributes();
		return switch (socialType) {
			case KAKAO -> processKakaoUser(attributes);
			case NAVER -> processNaverUser(attributes);
			case GOOGLE -> processGoogleUser(attributes);
			default -> throw new OAuth2AuthenticationException("Unsupported provider: " + socialType);
		};
	}

	private OAuth2User processGoogleUser(Map<String, Object> attributes) {

		return CustomOAuth2User.ofGoogle(
			Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
			attributes,
			attributes.get("name").toString(),
			attributes.get("email").toString(),
			attributes.get("picture").toString(),
			attributes.get("sub").toString()
		);
	}

	private OAuth2User processNaverUser(Map<String, Object> attributes) {

		Map<String, Object> response = (Map<String, Object>)attributes.get("response");

		return CustomOAuth2User.ofNaver(
			Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
			attributes,
			response.get("nickname").toString(),
			response.get("email").toString(),
			response.get("profile_image").toString(),
			response.get("id").toString()
		);
	}

	private OAuth2User processKakaoUser(Map<String, Object> attributes) {

		Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>)kakaoAccount.get("profile");

		return CustomOAuth2User.ofKakao(
			Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
			attributes,
			profile.get("nickname").toString(),
			kakaoAccount.get("email").toString(),
			profile.get("thumbnail_image_url").toString(),
			attributes.get("id").toString()
		);
	}
}
