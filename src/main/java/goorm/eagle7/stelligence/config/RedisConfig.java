package goorm.eagle7.stelligence.config;

import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.*;

import java.time.Duration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis와 Cache 관련 설정 클래스입니다.
 */
@Configuration
@EnableCaching
public class RedisConfig {

	private static final int DEFAULT_EXPIRE_SEC = 60 * 5; //캐시 기본 유효 시간 5분

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
		return RedisCacheManager.builder(
				RedisCacheWriter.lockingRedisCacheWriter(factory)) //locking을 통해 캐시의 일관성을 보장
			.cacheDefaults(cacheConfiguration()) // 캐시 기본 설정
			.transactionAware() //Redis의 동작을 Spring이 관리하는 트랜잭션과 동기화
			.build();
	}

	private RedisCacheConfiguration cacheConfiguration() {
		return RedisCacheConfiguration.defaultCacheConfig()
			.serializeKeysWith(fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(fromSerializer(new GenericJackson2JsonRedisSerializer()))
			.entryTtl(Duration.ofSeconds(DEFAULT_EXPIRE_SEC)) //DEFAULT_EXPIRE_SEC 만큼 캐시 유지
			.disableCachingNullValues();
	}

}
