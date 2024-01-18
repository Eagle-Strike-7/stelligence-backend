package goorm.eagle7.stelligence.domain.document.content;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.common.merge.MergeService;
import goorm.eagle7.stelligence.config.mockdata.WithMockData;
import goorm.eagle7.stelligence.domain.document.content.dto.DocumentResponse;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.section.SectionRepository;

@SpringBootTest
@Transactional
@WithMockData
class DocumentContentServiceCachingTest {

	@Autowired
	private DocumentContentService documentContentService;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@MockBean
	private SectionRepository sectionRepository;

	@Autowired
	private MergeService mergeService;

	@AfterEach
	void tearDown() {
		//clear cache
		redisTemplate.delete("document::1");
	}

	@Test
	@DisplayName("캐싱 테스트")
	void cacheTest() {

		//문서 조회
		documentContentService.getDocument(1L);

		TestTransaction.flagForCommit();
		TestTransaction.end(); //트랜잭션 종료를 통한 캐싱 반영

		//캐시가 존재하는지 확인
		assertThat(redisTemplate.hasKey("document::1")).isTrue();

		TestTransaction.start();

		//merge 발생시 캐시 삭제
		mergeService.merge(1L, contribute());

		TestTransaction.flagForCommit();
		TestTransaction.end();

		//cache가 남아있는지 검증
		assertThat(redisTemplate.hasKey("document::1")).isFalse();
	}

	@Test
	@DisplayName("Evict 테스트")
	void cacheTest2() {

		//문서 조회 - 캐싱
		DocumentResponse documentResponse = documentContentService.getDocument(1L);

		TestTransaction.flagForCommit();
		TestTransaction.end(); //트랜잭션 종료를 통한 캐싱 반영

		//새 트랜잭션에서 문서 조회 : 캐싱 결과값 가져오기
		TestTransaction.start();

		DocumentResponse documentResponse2 = documentContentService.getDocument(1L);

		TestTransaction.flagForCommit();
		TestTransaction.end();

		//쿼리가 1번만 나갔는지 검증
		verify(sectionRepository, times(1)).findByVersion(any(Document.class), any(Long.class));

		//캐싱된 값이 같은지 검증
		assertThat(documentResponse.getDocumentId()).isEqualTo(documentResponse2.getDocumentId());
	}
}
