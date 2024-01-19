package goorm.eagle7.stelligence.config;

import static goorm.eagle7.stelligence.domain.document.content.dto.protobuf.DocumentResponseOuterClass.*;

import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * ProtoBufRedisSerializer
 * Redis에 저장할 때 DocumentResponse의 경우 Protocol Buffer를 통해 직렬화를 수행시킵니다.
 * 그 외의 경우에는 Jackson을 통해 직렬화를 수행합니다.
 *
 * 2023-01-19 일 기준으로 사용되지 않습니다.
 */
public class ProtoBufRedisSerializer extends GenericJackson2JsonRedisSerializer {

	@Override
	public byte[] serialize(Object value) throws SerializationException {
		//DocumentResponse 타입이라면 Protocol Buffer로 직렬화
		if (value instanceof DocumentResponse documentResponse) {
			return documentResponse.toByteArray();
		} else {
			//그 외의 경우 Jackson으로 직렬화
			return super.serialize(value);
		}
	}

	@Override
	public Object deserialize(byte[] source) throws SerializationException {
		try {
			// 먼저 DocumentResponse로 역직렬화 시도
			// 비효율적이므로 추후 로직 변경 예정
			return DocumentResponse.parseFrom(source);
		} catch (Exception e) {
			// 실패한 경우, JSON으로 역직렬화
			return super.deserialize(source);
		}
	}
}
