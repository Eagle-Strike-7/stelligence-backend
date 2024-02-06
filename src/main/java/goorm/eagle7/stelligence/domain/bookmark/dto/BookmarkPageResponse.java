package goorm.eagle7.stelligence.domain.bookmark.dto;

import java.util.List;

import org.springframework.data.domain.Slice;

import goorm.eagle7.stelligence.domain.bookmark.model.Bookmark;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookmarkPageResponse {

	private boolean hasNext;
	private List<BookmarkSimpleResponse> bookmarks;

	public static BookmarkPageResponse from(Slice<Bookmark> slice) {
		boolean hasNext = slice.hasNext();
		List<BookmarkSimpleResponse> bookmarks =
			slice.getContent().stream()
				.map(BookmarkSimpleResponse::from)
				.toList();
		return new BookmarkPageResponse(hasNext, bookmarks);
	}

}
