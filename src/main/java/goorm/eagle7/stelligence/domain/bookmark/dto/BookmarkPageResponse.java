package goorm.eagle7.stelligence.domain.bookmark.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import goorm.eagle7.stelligence.domain.bookmark.model.Bookmark;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookmarkPageResponse {
	private List<BookmarkSimpleResponse> bookmarks;
	private int totalPages;

	public static BookmarkPageResponse from(Page<Bookmark> page) {
		List<BookmarkSimpleResponse> bookmarks =
			page
				.map(BookmarkSimpleResponse::from)
				.getContent();
		int totalPages = page.getTotalPages();
		return new BookmarkPageResponse(bookmarks, totalPages);
	}

}
