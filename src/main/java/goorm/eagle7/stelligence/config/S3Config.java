package goorm.eagle7.stelligence.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@PropertySource("classpath:aws.properties")
public class S3Config {

	@Value("${aws.s3.accessKey}")
	private String accessKey;

	@Value("${aws.s3.secretKey}")
	private String secretKey;

	@Bean
	public S3Client s3Client() {
		return S3Client.builder()
			.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
			.region(Region.AP_NORTHEAST_2)
			.build();
	}
}
