package goorm.eagle7.stelligence.common.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import goorm.eagle7.stelligence.config.JpaAuditingConfig;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.PersistenceContext;

/**
 * BaseTimeEntityTest
 * TimeAuditing이 잘 동작하는지 테스트합니다.
 */
@DataJpaTest
@Disabled
@Import(JpaAuditingConfig.class)
class BaseTimeEntityTest {

	@PersistenceContext
	private EntityManager em;

	@Entity
	static class TestEntity extends BaseTimeEntity {
		@Id
		Long id;
		String val;
	}

	@Test
	void createdAt() {
		TestEntity testEntity = new TestEntity();
		testEntity.id = 1L;

		em.persist(testEntity);

		assertThat(testEntity.createdAt).isNotNull(); //생성 시간은 null 이 아니다.
		assertThat(testEntity.updatedAt).isNotNull(); //수정 시간은 null 이 아니다.
		assertThat(testEntity.createdAt).isEqualTo(testEntity.updatedAt); //생성 시간과 수정 시간은 같다.
	}

	@Test
	void updatedAt() {
		//생성
		TestEntity testEntity = new TestEntity();
		testEntity.id = 1L;

		em.persist(testEntity);

		em.flush();
		em.clear();

		//수정
		TestEntity findTestEntity = em.find(TestEntity.class, 1L);
		findTestEntity.val = "hello";

		em.persist(findTestEntity);

		em.flush();
		em.clear();

		//검증
		TestEntity updatedTestEntity = em.find(TestEntity.class, 1L);
		assertThat(updatedTestEntity.createdAt).isNotNull(); //생성 시간은 null 이 아니다.
		assertThat(updatedTestEntity.updatedAt).isNotNull(); //수정 시간은 null 이 아니다.
		assertThat(updatedTestEntity.createdAt).isNotEqualTo(updatedTestEntity.updatedAt); //생성 시간과 수정 시간은 다르다.
	}
}