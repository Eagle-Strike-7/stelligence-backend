package goorm.eagle7.stelligence.domain.section;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import goorm.eagle7.stelligence.config.mockdata.WithMockData;
import goorm.eagle7.stelligence.domain.section.model.Section;

@SpringBootTest
@WithMockData
class SectionRepositoryTest {

	@Autowired
	private SectionRepository sectionRepository;

	@Test
	@DisplayName("최신 섹션 조회 - 성공")
	void findLatestSectionSuccess() {
		Section latestSection = sectionRepository.findLatestSection(1L);

		assertThat(latestSection.getId()).isEqualTo(1L);
		assertThat(latestSection.getRevision()).isEqualTo(3L);
	}

	@Test
	@DisplayName("최신 섹션 조회 - 실패")
	void findLatestSectionFail() {
		Section latestSection = sectionRepository.findLatestSection(9999999L);

		assertThat(latestSection).isNull();
	}
}