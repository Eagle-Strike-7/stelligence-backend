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
import lombok.extern.slf4j.Slf4j;

/**
 * DocumentCacheAspect
 *
 * Document 조회 시 캐싱을 적용합니다.
 * AOP를 사용하여, DocumentContentService.getDocument 메서드의 호출 전후에 작업을 추가합니다.
 *
 * 캐시를 저장할 때 Protocol Buffer 방식을 적용하여 직렬화를 하기 위해 사용됩니다.
 * Protocol Buffer를 사용하면 Jackson을 통한 JSON 직렬화가 불가능해지므로,
 * 반환 타입을 DocumentResponse로 바꾸어야 합니다.
 *
 * 이 방식을 통해 캐시 저장의 공간을 효율적으로 사용할 수 있으며,
 * 다른 곳으로부터는 Protocol Buffer를 사용하고 있음을 숨길 수 있습니다.
 *
 * 저장된 캐시는 수정요청이 반영되는 경우 유효하지 않게 되므로 삭제되어야 합니다. - MergeService.java 참조
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DocumentCacheAspect {

	private final RedisTemplate<String, byte[]> redisTemplate;
	private static final String DOCUMENT_CACHE_KEY_TEMPLATE = "document::%s";

	/**
	 * Document 조회 시 캐싱을 적용합니다.
	 * @param joinPoint DocumentContentService.getDocument 메서드 호출을 가로챈 JoinPoint
	 * @return DocumentResponse
	 * @throws Throwable 서비스 로직 호출시 발생한 예외
	 */
	@Around("execution(* goorm.eagle7.stelligence.domain.document.content.DocumentContentService.getDocument(Long))")
	public Object getDocumentProxy(ProceedingJoinPoint joinPoint) throws Throwable {
		log.trace("DocumentCacheAspect.getDocumentProxy called");

		// Redis 키 생성
		Long documentId = (Long)joinPoint.getArgs()[0];
		String key = String.format(DOCUMENT_CACHE_KEY_TEMPLATE, documentId);

		// 캐시 조회
		byte[] cachedDocument = redisTemplate.opsForValue().get(key);

		// 캐시가 존재한다면 캐시 데이터를 DocumentResponse로 변환하여 반환
		if (cachedDocument != null) {
			log.debug("{} cache hit", key);

			// 바이너리 캐시 데이터를 ProtoBufDocumentResponse로 변환
			DocumentResponseOuterClass.DocumentResponse documentResponse = DocumentResponseOuterClass.DocumentResponse.parser()
				.parseFrom(cachedDocument);

			// ProtoBufDocumentResponse를 DocumentResponse로 변환
			return ProtoBufDocumentResponseConverter.toMyDocumentResponse(documentResponse);
		} else { // 캐시가 없다면 DB 조회 후 캐시 저장
			log.debug("{} cache miss", key);
			Object document = joinPoint.proceed(); // DocumentContentService.getDocument 메서드 호출 후 반환값을 저장

			// 서비스 메서드의 반환값을 캐시에 담기 위하여 ProtoBufDocumentResponse로 변환
			DocumentResponseOuterClass.DocumentResponse protoBufDocumentResponse = ProtoBufDocumentResponseConverter.toProtoBufDocumentResponse(
				(DocumentResponse)document);

			// 캐시 저장
			redisTemplate.opsForValue().set(key, protoBufDocumentResponse.toByteArray());

			// 서비스 메서드의 반환값 반환
			return document;
		}
	}
}
