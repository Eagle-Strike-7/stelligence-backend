package goorm.eagle7.stelligence.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis와 Cache 관련 설정 클래스입니다.
 */
@Configuration
@EnableCaching
public class RedisConfig {

	/**
	 * RedisTemplate 설정
	 * String 타입의 key와 byte[] 타입의 value를 사용합니다.
	 * @param factory
	 * @return
	 */
	@Bean
	public RedisTemplate<String, byte[]> redisTemplate(RedisConnectionFactory factory) {
		RedisTemplate<String, byte[]> template = new RedisTemplate<>();
		template.setConnectionFactory(factory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericToStringSerializer<>(byte[].class));

		return template;
	}
}
