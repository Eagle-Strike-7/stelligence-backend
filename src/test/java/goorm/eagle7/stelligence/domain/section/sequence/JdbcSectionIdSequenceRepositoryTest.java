package goorm.eagle7.stelligence.domain.section.sequence;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class JdbcSectionIdSequenceRepositoryTest {

	@Autowired
	JdbcSectionIdSequenceRepository repository;

	@Test
	@DisplayName("기본 동작 테스트")
	void basic() {
		Long documentId = 201L;

		repository.createSequence(documentId);

		Long sectionId = repository.getAndIncrementSectionId(documentId);

		Long sectionId2 = repository.getAndIncrementSectionId(documentId);

		assertThat(sectionId).isEqualTo(1L);
		assertThat(sectionId2).isEqualTo(2L);
	}

	public static Long sum = 0L;

	/**
	 * SectionIdSequence가 서로다른 스레드의 요청에서도 잘 동작하는지 확인합니다.
	 * @throws InterruptedException
	 */
	@Test
	@DisplayName("동시성 테스트")
	void concurrency() throws InterruptedException {
		Long documentId = 200L;

		repository.createSequence(documentId);

		//repository.getAndIncrementSectionId(documentId); 를 1번 호출하는 스레드를 100개 생성

		List<Thread> threads = new ArrayList<>();

		//각각의 쓰레드는 repository.getAndIncrementSectionId(documentId); 를 1번 호출합니다.
		//이후 나온 값의 합이 5050이 나오는지 확인합니다.
		Runnable query = () -> {
			Long sectionId = repository.getAndIncrementSectionId(documentId);
			sum += sectionId;
		};

		for (int i = 0; i < 100; i++) {
			threads.add(new Thread(query));
		}

		threads.forEach(Thread::start);

		for (Thread thread : threads) {
			thread.join();
		}

		Long sectionId = repository.getAndIncrementSectionId(documentId);
		assertThat(sectionId).isEqualTo(101L); //101번째 요청의 결과는 101이 나와야 합니다.
		assertThat(sum).isEqualTo(5050L); // 1~100까지의 합은 5050이 나와야 합니다.

	}

}