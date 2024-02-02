package goorm.eagle7.stelligence.domain.bookmark.dto;

import goorm.eagle7.stelligence.domain.bookmark.model.Bookmark;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookmarkSimpleResponse {
	private Long bookmarkId;
	private Long documentId;
	private String documentTitle;

	public static BookmarkSimpleResponse from(Bookmark bookmark) {
		return new BookmarkSimpleResponse(
			bookmark.getId(),
			bookmark.getDocument().getId(),
			bookmark.getDocument().getTitle()
		);
	}

}
