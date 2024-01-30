package goorm.eagle7.stelligence.domain.document.content;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import goorm.eagle7.stelligence.config.mockdata.WithMockData;
import goorm.eagle7.stelligence.domain.member.model.Member;

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

	@Test
	@DisplayName("문서 기여자 조회")
	void findContributorsByDocumentId() {
		List<Member> contributors1 = documentContentRepository.findContributorsByDocumentId(1L);

		//1번 문서에는 1, 2번 사용자가 기여했다.
		assertThat(contributors1).hasSize(2);

		assertThat(contributors1.get(0).getId()).isEqualTo(1L);
		assertThat(contributors1.get(1).getId()).isEqualTo(2L);

		List<Member> contributors2 = documentContentRepository.findContributorsByDocumentId(2L);

		//2번 문서에는 3번 사용자가 기여했다.
		assertThat(contributors2).hasSize(1);

		assertThat(contributors2.get(0).getId()).isEqualTo(3L);

	}
}