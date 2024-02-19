package goorm.eagle7.stelligence.common.auth.oauth;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import goorm.eagle7.stelligence.domain.member.model.SocialType;
import lombok.Getter;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

	private String nickname;
	private String email;
	private String imageUrl;
	private String socialId;
	private SocialType socialType;

	/**
	 * 	OAuth2User: 각 소셜 로그인 서비스에서 제공하는 정보가 담긴 DefaultOAuth2User 객체
	 * 		- OAuth2UserService - loadUser 메서드를 통해 반환된 OAuth2User는 Authentication 객체의 일부(principal)로 SecurityContextHolder에 자동 저장됨.
	 *
	 * @param authorities ROLE_USER 권한을 가진 SimpleGrantedAuthority 객체 (다중)
	 * @param attributes  각 소셜 로그인 서비스에서 제공하는 정보가 담긴 Map
	 * @param nameAttributeKey 소셜 로그인 제공자가 제공하는 사용자 정보(JSON 형태)에서 사용자의 고유 식별자(예: 사용자 ID)를 추출하는 데 사용되는 PK 필드의 이름
	 */
	private CustomOAuth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes,
		String nameAttributeKey) {
		super(authorities, attributes, nameAttributeKey);
	}

	// 구글 로그인 시
	public static CustomOAuth2User ofGoogle(Collection<? extends GrantedAuthority> authorities,
		Map<String, Object> attributes, String name, String email, String imageUrl, String socialId,
		SocialType socialType
	) {

		CustomOAuth2User customOAuth2User = new CustomOAuth2User(authorities, attributes, "sub");

		customOAuth2User.nickname = name; // name을 nickname으로 사용.
		customOAuth2User.email = email;
		customOAuth2User.imageUrl = imageUrl;
		customOAuth2User.socialId = socialId;
		customOAuth2User.socialType = socialType;

		return customOAuth2User;

	}

	// 네이버 로그인 시
	public static CustomOAuth2User ofNaver(Collection<? extends GrantedAuthority> authorities,
		Map<String, Object> attributes, String nickname, String email, String imageUrl, String socialId,
		SocialType socialType) {

		CustomOAuth2User customOAuth2User = new CustomOAuth2User(authorities, attributes, "response");

		customOAuth2User.nickname = nickname;
		customOAuth2User.email = email;
		customOAuth2User.imageUrl = imageUrl;
		customOAuth2User.socialId = socialId;
		customOAuth2User.socialType = socialType;

		return customOAuth2User;
	}

	// 카카오 로그인 시
	public static CustomOAuth2User ofKakao(Collection<? extends GrantedAuthority> authorities,
		Map<String, Object> attributes, String nickname, String email, String imageUrl, String socialId, SocialType socialType) {

		CustomOAuth2User customOAuth2User = new CustomOAuth2User(authorities, attributes, "properties");

		customOAuth2User.nickname = nickname;
		customOAuth2User.email = email;
		customOAuth2User.imageUrl = imageUrl;
		customOAuth2User.socialId = socialId;
		customOAuth2User.socialType = socialType;

		return customOAuth2User;
	}

	/**
	 * CustomOAuth2User의 equals 메서드를 오버라이딩하여, CustomOAuth2User의 소셜 ID, SocailType이 같으면 같은 객체로 판단하도록 함.
	 * -> 중복 회원 가입 방지
	 * TODO 같은 객체로 판단할 때, 다른 필드 변경 시 DB 업데이트 확인 필요
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		CustomOAuth2User that = (CustomOAuth2User) o;
		return Objects.equals(socialId, that.socialId) && Objects.equals(socialType, that.socialType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), socialId, socialType);
	}
}
