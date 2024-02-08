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

	@Test
	@DisplayName("[성공] 활성 멤버가 작성한 문서 전체 조회 - countDistinctByAuthor_Id")
	void countDistinctByAuthor_IdActiveSuccess() {

		// 활성 멤버
		// 	-- 1번 멤버는 11, 12, 13, 14번 문서를 작성하였습니다.
		// 	-- 2번 멤버는 15, 16, 18번 문서를 작성하였습니다.
		// 	-- 3번 멤버는 17번 문서를 작성하였습니다.
		// 	-- 4번 멤버는 19번 문서를 작성하였습니다.

		// when
		long active1Count = documentContentRepository.countDistinctByAuthor_Id(1L);
		long active2Count = documentContentRepository.countDistinctByAuthor_Id(2L);
		long active3Count = documentContentRepository.countDistinctByAuthor_Id(3L);
		long active4Count = documentContentRepository.countDistinctByAuthor_Id(4L);

		// then
		assertThat(active1Count).isEqualTo(4);
		assertThat(active2Count).isEqualTo(3);
		assertThat(active3Count).isEqualTo(1);
		assertThat(active4Count).isEqualTo(1);

	}

	@Test
	@DisplayName("[성공] 탈퇴 멤버가 작성한 문서 전체 조회 - countDistinctByAuthor_Id")
	void countDistinctByAuthor_IdExpiredSuccess() {

		// 탈퇴 멤버
		// 	-- 5번 멤버는 20, 23, 24번 문서를 작성하였습니다.
		// 	-- 6번 멤버는 21번 문서를 작성하였습니다.
		// 	-- 7번 멤버는 22번 문서를 작성하였습니다.

		// when
		long expired5Count = documentContentRepository.countDistinctByAuthor_Id(5L);
		long expired6Count = documentContentRepository.countDistinctByAuthor_Id(6L);
		long expired7Count = documentContentRepository.countDistinctByAuthor_Id(7L);

		// then
		assertThat(expired5Count).isEqualTo(3);
		assertThat(expired6Count).isEqualTo(1);
		assertThat(expired7Count).isEqualTo(1);
	}



}