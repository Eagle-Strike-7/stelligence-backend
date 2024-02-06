package goorm.eagle7.stelligence.domain.bookmark.dto;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Slice;

import goorm.eagle7.stelligence.domain.bookmark.model.Bookmark;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.member.model.Member;

@ExtendWith(MockitoExtension.class)
class BookmarkPageResponseTest {

	/**
	 * <h2>[정상] BookmarkPageResponse.from 테스트</h2>
	 * <p>결과: Slice<Bookmark>를 받아 BookmarkPageResponse로 변환</p>
	 * <p>검증 방식: hasNext, bookmarks 확인</p>
	 * <p>- hasNext가 true인지 확인</p>
	 * <p>- bookmarks의 size, 요소 타입 BookmarkSimpleResponse 확인</p>
	 */
	@Test
	@DisplayName("[정상] Slice<Bookmark>를 받아 BookmarkPageResponse로 변환 - from")
	void from() {

		// given
		Member member1 = member(1L, "nickname");
		Member member2 = member(2L, "nickname2");
		Document document = document(1L, member1, "title", 1L);

		Slice<Bookmark> slice = mock(Slice.class);
		when(slice.hasNext()).thenReturn(true);
		when(slice.getContent()).thenReturn(List.of(Bookmark.of(member1, document), Bookmark.of(member2, document)));

		// when
		BookmarkPageResponse bookmarkPageResponse = BookmarkPageResponse.from(slice);

		// then
		assertTrue(bookmarkPageResponse.isHasNext());
		assertEquals(2, bookmarkPageResponse.getBookmarks().size());
		assertInstanceOf(
			BookmarkSimpleResponse.class,
			bookmarkPageResponse.getBookmarks().get(0));

	}

}