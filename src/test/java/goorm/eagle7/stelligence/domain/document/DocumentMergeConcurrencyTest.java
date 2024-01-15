package goorm.eagle7.stelligence.domain.document;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.domain.TestConfig;
import goorm.eagle7.stelligence.domain.document.dto.SectionResponse;
import goorm.eagle7.stelligence.domain.document.model.Document;
import goorm.eagle7.stelligence.domain.section.SectionRepository;
import goorm.eagle7.stelligence.domain.section.model.Heading;

@SpringBootTest
@Transactional
@Import(TestConfig.class)
class DocumentMergeConcurrencyTest {

	@Autowired
	private DocumentService documentService;

	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	private SectionRepository sectionRepository;

	@AfterEach
	void tearDown() {
		sectionRepository.deleteAll();
	}

	@Test
	@DisplayName("Merge 동시성 테스트")
	void mergeConcurrency() {

		String title = "title";

		String rawContent =
			"# title1\n"
				+ "content1\n"
				+ "## title2\n"
				+ "content2 line 1\n"
				+ "content2 line 2\n"
				+ "### title3\n"
				+ "content3";

		Long documentId = documentService.createDocument(title, rawContent);

		//트랜잭션 종료를 통해 이후 쓰레드들이 정상적으로 동작할 수 있게 함
		//em.flush(), em.clear() 사용시, 쓰레드들이 정상적으로 동작하지 않음
		//DB에 정보는 있지만, 테스트 트랜잭션이 쓰기모드로 엔티티에 접근한 상태이기 때문에 Deadklock 발생
		//따라서 강제로 트랜잭션을 종료 시킨 이후, 쓰레드들이 각각의 트랜잭션에서 동작하게 함
		TestTransaction.flagForCommit();
		TestTransaction.end();

		Thread t1 = new Thread(() -> documentService.mergeContribute(
			documentId,
			List.of(
				new DocumentService.Commit("INSERT", 1L, 1L, Heading.H1, "newTitle", "newContent")
			)
		));

		Thread t2 = new Thread(() -> documentService.mergeContribute(
			documentId,
			List.of(
				new DocumentService.Commit("UPDATE", 1L, 1L, Heading.H1, "title1Update", "title1Update")
			)
		));

		t1.start();
		t2.start();

		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		TestTransaction.start(); // 지연로딩을 위한 트랜잭션 재시작

		Document document = documentRepository.findById(documentId).get();

		document.getSections().stream().map(SectionResponse::of).forEach(System.out::println);

		// 동시성 문제 발생시 2가 나옴
		//DocumentRepository.findForUpdate의 @Lock을 없애보면 확인 가능
		assertThat(document.getCurrentRevision()).isEqualTo(3L);

		assertThat(document.getSections()).hasSize(5);
		//아래 assert문은 동시성 문제 발생시 실패. 정렬이 되기 때문에 순서는 이렇게 보장. 2-2만 안나오면 됨
		assertThat(document.getSections().get(3).getRevision()).isEqualTo(2L);
		assertThat(document.getSections().get(4).getRevision()).isEqualTo(3L);

		TestTransaction.end();
	}
}
