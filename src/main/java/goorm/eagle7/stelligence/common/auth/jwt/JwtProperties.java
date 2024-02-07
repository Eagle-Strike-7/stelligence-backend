package goorm.eagle7.stelligence.common.auth.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

	private String secretKey;
	private String algorithmName;
	private Header header = new Header();
	private Token accessToken = new Token();
	private Token refreshToken = new Token();
	private Claim claim = new Claim();

	@Getter
	public static class Header {
		private String type;
		private String tokenType;
		private String algorithm;
		private String algorithmType;
	}

	@Getter
	public static class Token {
		private String name;
		private long expiration;
	}

	@Getter
	public static class Claim {
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
