package goorm.eagle7.stelligence.config;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

	@Value("${jwt.secretKey}")
	private String jwtSecret;

	@Bean
	public SecretKey jwtKey() {

		// jwtSecret을 Base64로 디코딩하여 바이트 배열로 변환
		byte[] decodedKey = Base64.getDecoder().decode(jwtSecret);

		// 생성된 바이트 배열로부터 SecretKey 생성
		return new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");

	}

}
