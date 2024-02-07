package goorm.eagle7.stelligence.common.auth.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

	private String secretKey;
	private String algorithmName;
	private Header header = new Header();
	private Token accessToken = new Token();
	private Token refreshToken = new Token();
	private Claims claims = new Claims();

	@Getter
	@Setter
	public static class Header {
		private String type;
		private String tokenType;
		private String algorithm;
		private String algorithmType;
	}

	@Getter
	@Setter
	public static class Token {
		private String name;
		private long expiration;
	}

	@Getter
	@Setter
	public static class Claims {
		private String role;
		private String value;
	}

	public long getAccessTokenExpirationMs() {
		return accessToken.expiration * 60 * 1000;
	}

	public long getRefreshTokenExpirationMs() {
		return refreshToken.expiration * 60 * 1000;
	}

}
