package goorm.eagle7.stelligence.domain.section.sequence;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled
class JdbcSectionIdSequenceRepositoryTest {

	@Autowired
	JdbcSectionIdSequenceRepository repository;

	@Test
	@DisplayName("기본 동작 테스트")
	void basic() {
		Long documentId = 1L;

		repository.createSequence(1L);

		Long sectionId = repository.getAndIncrementSectionId(documentId);

		Long sectionId2 = repository.getAndIncrementSectionId(documentId);

		assertThat(sectionId).isEqualTo(1L);
		assertThat(sectionId2).isEqualTo(2L);
	}

	@Test
	@DisplayName("동시성 테스트")
	void concurrency() throws InterruptedException {
		Long documentId = 1L;

		repository.createSequence(1L);

		//repository.getAndIncrementSectionId(documentId); 를 1번 호출하는 스레드를 100개 생성

		List<Thread> threads = new ArrayList<>();

		Runnable query = () -> {
			Long sectionId = repository.getAndIncrementSectionId(documentId);
			System.out.println("sectionId = " + sectionId);
		};

		for (int i = 0; i < 10; i++) {
			threads.add(new Thread(query));
		}

		threads.forEach(Thread::start);

		for (Thread thread : threads) {
			thread.join();
		}

		Long sectionId = repository.getAndIncrementSectionId(documentId);

	}

}