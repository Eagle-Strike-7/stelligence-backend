package goorm.eagle7.stelligence.domain.section;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import goorm.eagle7.stelligence.domain.section.model.Section;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SectionRepositoryTest {

	@Autowired
	private SectionRepository sectionRepository;

	@Test
	@DisplayName("최신 섹션 조회 - 성공")
	void findLatestSectionSuccess() {
		Section latestSection = sectionRepository.findLatestSection(1L)
			.orElseThrow(() -> new RuntimeException("존재하지 않는 섹션입니다."));

		assertThat(latestSection.getId()).isEqualTo(1L);
		assertThat(latestSection.getRevision()).isEqualTo(3L);
	}

	@Test
	@DisplayName("최신 섹션 조회 - 실패")
	void findLatestSectionFail() {
		Optional<Section> latestSection = sectionRepository.findLatestSection(9999999L);

		assertThat(latestSection).isEmpty();
	}
}