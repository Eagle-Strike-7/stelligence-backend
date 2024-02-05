package goorm.eagle7.stelligence.domain.bookmark;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator;
import goorm.eagle7.stelligence.config.mockdata.WithMockData;
import goorm.eagle7.stelligence.domain.bookmark.model.Bookmark;
import goorm.eagle7.stelligence.domain.member.model.Member;

@DataJpaTest
@WithMockData
class BookmarkRepositoryTest {

	@Autowired
	private BookmarkRepository bookmarkRepository;

	private Bookmark bookmark1;
	private Bookmark bookmark2;
	private Bookmark bookmark3;
	private Bookmark bookmark4;

	@BeforeEach
	void setUp() {

		Member member1 = member(1L, "nickname");

		// given - memberId가 1L인 북마크 총 4개
		bookmark1 = bookmarkRepository.findById(1L).get();
		bookmark2 = bookmarkRepository.findById(5L).get();
		bookmark3 = bookmarkRepository.findById(8L).get();
		bookmark4 = bookmarkRepository.findById(10L).get();

	}

	// @Test
	// void existsByMemberIdAndDocumentId() {
	//
	// }

	@Test
	@DisplayName("[정상] 특정 사용자의 북마크 조회, 순서 확인 - findSliceByMemberIdWithPageable")
	void findSliceByMemberIdWithPageable() {

		// given

		// when
		Slice<Bookmark> bookmarkSlice = bookmarkRepository.findSliceByMemberIdWithPageable(1L,
			PageRequest.of(0, 10));

		// then
		assertNotNull(bookmarkSlice);
		assertEquals(4, bookmarkSlice.getContent().size());
		// bookmarkId 기준 순서
		assertThat(bookmarkSlice.getContent()).containsExactly(
			bookmark1, bookmark2, bookmark3, bookmark4
		);
	}
	//
	// @Test
	// void findByMemberIdAndDocumentId() {
	// }
	//
	// @Test
	// void deleteByMemberIdAndDocumentId() {
	// }
}