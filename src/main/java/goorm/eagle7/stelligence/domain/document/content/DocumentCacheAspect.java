package goorm.eagle7.stelligence.domain.document.content;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.document.content.dto.DocumentResponse;
import goorm.eagle7.stelligence.domain.document.content.dto.protobuf.DocumentResponseOuterClass;
import goorm.eagle7.stelligence.domain.document.content.dto.protobuf.converter.ProtoBufDocumentResponseConverter;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class DocumentCacheAspect {

	private final RedisTemplate<String, byte[]> redisTemplate;
	private static final String DOCUMENT_CACHE_KEY_TEMPLATE = "document::%s";

	//최신 document를 조회하는데에 캐싱을 적용합니다.
	@Around("execution(* goorm.eagle7.stelligence.domain.document.content.DocumentContentService.getDocument(Long))")
	public Object getDocumentProxy(ProceedingJoinPoint joinPoint) throws Throwable {

		// Redis 키 생성
		Long documentId = (Long)joinPoint.getArgs()[0];
		String key = String.format(DOCUMENT_CACHE_KEY_TEMPLATE, documentId);

		// 캐시 조회
		byte[] cachedDocument = redisTemplate.opsForValue().get(key);

		// 캐시가 존재한다면 캐시 데이터를 DocumentResponse로 변환하여 반환
		if (cachedDocument != null) {
			DocumentResponseOuterClass.DocumentResponse documentResponse = DocumentResponseOuterClass.DocumentResponse.parser()
				.parseFrom(cachedDocument);

			return ProtoBufDocumentResponseConverter.toMyDocumentResponse(documentResponse);
		} else { // 캐시가 없다면 DB 조회 후 캐시 저장
			Object document = joinPoint.proceed();
			DocumentResponseOuterClass.DocumentResponse protoBufDocumentResponse = ProtoBufDocumentResponseConverter.toProtoBufDocumentResponse(
				(DocumentResponse)document);

			redisTemplate.opsForValue().set(key, protoBufDocumentResponse.toByteArray());
			return document;
		}
	}
}
