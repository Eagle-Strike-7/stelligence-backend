package goorm.eagle7.stelligence.domain.document.content;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import goorm.eagle7.stelligence.config.mockdata.WithMockData;

@DataJpaTest
@WithMockData
class DocumentContentRepositoryTest {

	@Autowired
	private DocumentContentRepository documentContentRepository;

	@Test
	@DisplayName("특정 문자열 포함 documentId 찾기 테스트 - 전체")
	void findDocumentIdWhereContentContains() {
		List<Long> test = documentContentRepository.findDocumentIdWhichContainsKeywordInLatestVersion("");

		assertThat(test).hasSize(4);
	}

	@Test
	@DisplayName("특정 문자열 포함 documentId 찾기 테스트 - 삭제된 섹션은 조회의 대상이 되어서는 안된다.")
	void findDocumentIdWhereContentContainsDelete() {
		List<Long> test = documentContentRepository.findDocumentIdWhichContainsKeywordInLatestVersion("content1");

		//1의 content1은 삭제되었으므로, 2, 3, 4만 조회되어야 한다.
		assertThat(test).containsExactly(2L, 3L, 4L);
	}
}